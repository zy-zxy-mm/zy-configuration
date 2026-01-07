<template>
  <div class="panel-runtime-container">
    <!-- 顶部信息栏 -->
    <div class="runtime-header" v-if="showHeader">
      <div class="header-left">
        <a-button @click="handleBack" v-if="showBackButton">
          <template #icon><LeftOutlined /></template>
          返回
        </a-button>
        <span class="page-title">{{ pageInfo?.pageName || '控制面板' }}</span>
      </div>
      <div class="header-right">
        <a-tag v-if="wsConnected" color="success">
          <template #icon><CheckCircleOutlined /></template>
          已连接
        </a-tag>
        <a-tag v-else color="error">
          <template #icon><CloseCircleOutlined /></template>
          未连接
        </a-tag>
        <a-button size="small" @click="handleRefresh" :loading="refreshing">
          <template #icon><ReloadOutlined /></template>
        </a-button>
      </div>
    </div>
    
    <!-- 设备绑定提示 -->
    <div class="device-binding-bar" v-if="!deviceId && !isEmbedded">
      <a-alert type="warning" show-icon>
        <template #message>
          <span>未绑定设备，请通过URL参数传入设备ID：</span>
          <code>?deviceId=xxx</code>
        </template>
      </a-alert>
    </div>
    
    <!-- 画布区域 -->
    <div class="runtime-canvas-wrapper">
      <div 
        class="runtime-canvas"
        :style="canvasStyle"
      >
        <div
          v-for="widget in widgets"
          :key="widget.id"
          class="runtime-widget"
          :style="getWidgetStyle(widget)"
        >
          <WidgetRenderer 
            :widget="widget" 
            :isDesign="false"
            :shadowState="shadowState"
            :deviceId="deviceId"
            @command="handleCommand"
            @httpRequest="handleHttpRequest"
          />
        </div>
      </div>
    </div>
    
    <!-- 加载状态 -->
    <div class="loading-overlay" v-if="loading">
      <a-spin size="large" tip="加载中..." />
    </div>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message, Modal } from 'ant-design-vue';
import { 
  LeftOutlined, CheckCircleOutlined, CloseCircleOutlined, ReloadOutlined 
} from '@ant-design/icons-vue';
import { getPageById, getPublishedConfig, getVersionConfig } from './api/panel.api';
import WidgetRenderer from './components/WidgetRenderer.vue';
import { defHttp } from '/@/utils/http/axios';

defineOptions({ name: 'ConfigurationPanelRuntime' });

const route = useRoute();
const router = useRouter();

// URL参数
const pageId = ref(route.query.pageId as string);
const versionId = ref(route.query.versionId as string);
const deviceId = ref(route.query.deviceId as string || '');
const isEmbedded = ref(route.query.embedded === 'true');
const showHeader = computed(() => !isEmbedded.value);
const showBackButton = computed(() => !isEmbedded.value);

// 状态
const loading = ref(true);
const refreshing = ref(false);
const pageInfo = ref<any>(null);
const widgets = ref<any[]>([]);
const shadowState = reactive<Record<string, any>>({});
const wsConnected = ref(false);

// WebSocket
let ws: WebSocket | null = null;
let reconnectTimer: any = null;
let heartbeatTimer: any = null;

// 画布样式
const canvasStyle = computed(() => ({
  width: `${pageInfo.value?.canvasWidth || 390}px`,
  height: `${pageInfo.value?.canvasHeight || 844}px`,
}));

onMounted(async () => {
  await loadConfig();
  if (deviceId.value) {
    await fetchShadowState();
    connectWebSocket();
  }
  loading.value = false;
});

onUnmounted(() => {
  disconnectWebSocket();
});

// 加载配置
async function loadConfig() {
  if (!pageId.value) return;
  
  try {
    // 加载页面信息
    pageInfo.value = await getPageById(pageId.value);
    
    // 加载配置JSON
    let configJson: string;
    if (versionId.value) {
      // 预览指定版本
      configJson = await getVersionConfig(pageId.value, versionId.value);
    } else {
      // 运行已发布版本
      configJson = await getPublishedConfig(pageId.value);
    }
    
    if (configJson) {
      const config = typeof configJson === 'string' ? JSON.parse(configJson) : configJson;
      widgets.value = config.widgets || [];
      
      // 更新画布尺寸
      if (config.canvasWidth) pageInfo.value.canvasWidth = config.canvasWidth;
      if (config.canvasHeight) pageInfo.value.canvasHeight = config.canvasHeight;
    }
  } catch (e) {
    console.error('加载配置失败:', e);
    message.error('加载面板配置失败');
  }
}

// 获取影子状态
async function fetchShadowState() {
  if (!deviceId.value) return;
  
  try {
    const res = await defHttp.get({
      url: '/iot/shadow/snapshot',
      params: { deviceIds: deviceId.value },
    });
    
    if (res && res[deviceId.value]) {
      Object.assign(shadowState, res[deviceId.value]);
    }
  } catch (e) {
    console.error('获取设备状态失败:', e);
  }
}

// WebSocket连接
function connectWebSocket() {
  if (!deviceId.value) return;
  
  const token = localStorage.getItem('token') || '';
  const wsUrl = `${location.protocol === 'https:' ? 'wss:' : 'ws:'}//${location.host}/ws/iot?token=${token}`;
  
  try {
    ws = new WebSocket(wsUrl);
    
    ws.onopen = () => {
      wsConnected.value = true;
      // 订阅设备状态
      ws?.send(JSON.stringify({
        type: 'subscribe',
        subs: [{ deviceId: deviceId.value, points: ['*'] }],
      }));
      // 启动心跳
      startHeartbeat();
    };
    
    ws.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        if (data.type === 'delta' && data.deviceId === deviceId.value) {
          Object.assign(shadowState, data.values);
        }
      } catch (e) {
        console.error('解析WebSocket消息失败:', e);
      }
    };
    
    ws.onclose = () => {
      wsConnected.value = false;
      stopHeartbeat();
      // 自动重连
      reconnectTimer = setTimeout(() => {
        connectWebSocket();
      }, 5000);
    };
    
    ws.onerror = (error) => {
      console.error('WebSocket错误:', error);
      wsConnected.value = false;
    };
  } catch (e) {
    console.error('WebSocket连接失败:', e);
  }
}

function disconnectWebSocket() {
  if (reconnectTimer) {
    clearTimeout(reconnectTimer);
    reconnectTimer = null;
  }
  stopHeartbeat();
  if (ws) {
    ws.close();
    ws = null;
  }
}

function startHeartbeat() {
  heartbeatTimer = setInterval(() => {
    if (ws?.readyState === WebSocket.OPEN) {
      ws.send(JSON.stringify({ type: 'ping' }));
    }
  }, 30000);
}

function stopHeartbeat() {
  if (heartbeatTimer) {
    clearInterval(heartbeatTimer);
    heartbeatTimer = null;
  }
}

// 组件样式
function getWidgetStyle(widget: any) {
  return {
    left: `${widget.x}px`,
    top: `${widget.y}px`,
    width: `${widget.width}px`,
    height: `${widget.height}px`,
    zIndex: widget.style?.zIndex || 1,
  };
}

// 处理命令
async function handleCommand(command: any) {
  try {
    const res = await defHttp.post({
      url: '/iot/command',
      data: {
        traceId: generateTraceId(),
        deviceId: command.deviceId || deviceId.value,
        commandKey: command.commandKey,
        params: command.params,
      },
    });
    
    if (res.success) {
      message.success('命令发送成功');
    } else {
      message.error(res.message || '命令发送失败');
    }
  } catch (e: any) {
    message.error(e.message || '命令发送失败');
  }
}

// 处理HTTP请求事件
async function handleHttpRequest(request: any) {
  const { eventKey, config, params } = request;
  
  // 确认弹窗
  if (config.confirmEnabled) {
    Modal.confirm({
      title: '确认操作',
      content: config.confirmText || '确定执行此操作吗？',
      onOk: () => executeHttpRequest(config, params),
    });
  } else {
    await executeHttpRequest(config, params);
  }
}

// 执行HTTP请求
async function executeHttpRequest(config: any, params: Record<string, any>) {
  try {
    // 替换URL中的变量
    let url = replaceVariables(config.url, params);
    
    // 构建请求体
    let body: any = null;
    if (config.method !== 'GET' && config.bodyTemplate) {
      const bodyStr = replaceVariables(config.bodyTemplate, params);
      try {
        body = JSON.parse(bodyStr);
      } catch {
        body = bodyStr;
      }
    }
    
    // 发送请求
    const response = await fetch(url, {
      method: config.method,
      headers: config.headers || {},
      body: body ? JSON.stringify(body) : undefined,
    });
    
    if (response.ok) {
      message.success(config.successMessage || '操作成功');
    } else {
      message.error(config.errorMessage || '操作失败');
    }
  } catch (e: any) {
    console.error('HTTP请求失败:', e);
    message.error(config.errorMessage || e.message || '操作失败');
  }
}

// 替换变量
function replaceVariables(template: string, params: Record<string, any>): string {
  return template.replace(/\$\{(\w+)\}/g, (match, key) => {
    return params[key] !== undefined ? String(params[key]) : match;
  });
}

// 生成追踪ID
function generateTraceId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2, 9);
}

// 刷新
async function handleRefresh() {
  refreshing.value = true;
  await fetchShadowState();
  refreshing.value = false;
  message.success('刷新成功');
}

// 返回
function handleBack() {
  router.back();
}
</script>

<style lang="less" scoped>
.panel-runtime-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
  position: relative;
}

.runtime-header {
  height: 50px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .page-title {
      font-size: 16px;
      font-weight: 500;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.device-binding-bar {
  padding: 8px 16px;
  
  code {
    background: #f5f5f5;
    padding: 2px 6px;
    border-radius: 4px;
    margin-left: 8px;
  }
}

.runtime-canvas-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  padding: 20px;
}

.runtime-canvas {
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  position: relative;
  border-radius: 8px;
}

.runtime-widget {
  position: absolute;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
</style>
