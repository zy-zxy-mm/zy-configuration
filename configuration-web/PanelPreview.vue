<template>
  <div class="panel-preview-container">
    <!-- 顶部工具栏 -->
    <div class="preview-header">
      <div class="header-left">
        <a-button @click="handleBack">
          <template #icon><LeftOutlined /></template>
          返回
        </a-button>
        <span class="page-title">{{ pageInfo?.pageName || '预览' }} - 预览模式</span>
      </div>
      <div class="header-center">
        <a-radio-group v-model:value="previewDevice" button-style="solid" size="small">
          <a-radio-button value="mobile">
            <MobileOutlined /> 手机
          </a-radio-button>
          <a-radio-button value="tablet">
            <TabletOutlined /> 平板
          </a-radio-button>
          <a-radio-button value="desktop">
            <DesktopOutlined /> 桌面
          </a-radio-button>
        </a-radio-group>
      </div>
      <div class="header-right">
        <a-input 
          v-model:value="testDeviceId" 
          placeholder="输入测试设备ID" 
          style="width: 200px"
          size="small"
        />
        <a-button type="primary" size="small" @click="handleApplyDevice">应用</a-button>
      </div>
    </div>
    
    <!-- 预览区域 -->
    <div class="preview-content">
      <div class="device-frame" :class="previewDevice">
        <div class="device-screen">
          <div 
            class="preview-canvas"
            :style="canvasStyle"
          >
            <div
              v-for="widget in widgets"
              :key="widget.id"
              class="preview-widget"
              :style="getWidgetStyle(widget)"
            >
              <WidgetRenderer 
                :widget="widget" 
                :isDesign="false"
                :shadowState="mockShadowState"
                :deviceId="testDeviceId"
                @command="handleCommand"
                @httpRequest="handleHttpRequest"
              />
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 模拟数据面板 -->
    <div class="mock-panel">
      <div class="mock-header">
        <span>模拟数据</span>
        <a-button type="link" size="small" @click="resetMockData">重置</a-button>
      </div>
      <div class="mock-content">
        <a-form :label-col="{ span: 10 }" :wrapper-col="{ span: 14 }" size="small">
          <a-form-item v-for="(value, key) in mockShadowState" :key="key" :label="key">
            <a-input 
              v-if="typeof value === 'string'"
              v-model:value="mockShadowState[key]"
            />
            <a-input-number 
              v-else-if="typeof value === 'number'"
              v-model:value="mockShadowState[key]"
              style="width: 100%"
            />
            <a-switch 
              v-else-if="typeof value === 'boolean'"
              v-model:checked="mockShadowState[key]"
            />
          </a-form-item>
        </a-form>
        <a-button type="dashed" block size="small" @click="addMockField">
          + 添加字段
        </a-button>
      </div>
    </div>
    
    <!-- 添加字段弹窗 -->
    <a-modal v-model:open="addFieldVisible" title="添加模拟字段" @ok="confirmAddField">
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="字段名">
          <a-input v-model:value="newField.key" placeholder="如: temperature" />
        </a-form-item>
        <a-form-item label="类型">
          <a-select v-model:value="newField.type" style="width: 100%">
            <a-select-option value="string">字符串</a-select-option>
            <a-select-option value="number">数值</a-select-option>
            <a-select-option value="boolean">布尔值</a-select-option>
          </a-select>
        </a-form-item>
        <a-form-item label="初始值">
          <a-input v-model:value="newField.value" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { message } from 'ant-design-vue';
import { 
  LeftOutlined, MobileOutlined, TabletOutlined, DesktopOutlined 
} from '@ant-design/icons-vue';
import { getPageById, getPublishedConfig, getVersionConfig } from './api/panel.api';
import WidgetRenderer from './components/WidgetRenderer.vue';

defineOptions({ name: 'ConfigurationPanelPreview' });

const route = useRoute();
const router = useRouter();

const pageId = ref(route.query.pageId as string);
const versionId = ref(route.query.versionId as string);
const isLocalPreview = ref(route.query.preview === 'local');

const pageInfo = ref<any>(null);
const widgets = ref<any[]>([]);
const previewDevice = ref('mobile');
const testDeviceId = ref('test_device_001');

// 模拟影子状态
const mockShadowState = reactive<Record<string, any>>({
  power: true,
  temperature: 25.5,
  humidity: 60,
  tempSet: 26,
  mode: 'cool',
  fanSpeed: 'auto',
  brightness: 80,
  online: true,
});

// 添加字段
const addFieldVisible = ref(false);
const newField = reactive({
  key: '',
  type: 'string',
  value: '',
});

const canvasStyle = computed(() => {
  const width = pageInfo.value?.canvasWidth || 390;
  const height = pageInfo.value?.canvasHeight || 844;
  
  // 根据预览设备调整缩放
  let scale = 1;
  if (previewDevice.value === 'tablet') {
    scale = 1.2;
  } else if (previewDevice.value === 'desktop') {
    scale = 1.5;
  }
  
  return {
    width: `${width}px`,
    height: `${height}px`,
    transform: `scale(${scale})`,
    transformOrigin: 'top center',
  };
});

onMounted(async () => {
  await loadConfig();
});

async function loadConfig() {
  // 本地预览模式
  if (isLocalPreview.value) {
    const localConfig = localStorage.getItem('panel_preview_config');
    if (localConfig) {
      const config = JSON.parse(localConfig);
      widgets.value = config.widgets || [];
      pageInfo.value = {
        pageName: '本地预览',
        canvasWidth: config.canvasWidth || 390,
        canvasHeight: config.canvasHeight || 844,
      };
    }
    return;
  }
  
  if (!pageId.value) return;
  
  try {
    pageInfo.value = await getPageById(pageId.value);
    
    let configJson: string;
    if (versionId.value) {
      configJson = await getVersionConfig(pageId.value, versionId.value);
    } else {
      configJson = await getPublishedConfig(pageId.value);
    }
    
    if (configJson) {
      const config = typeof configJson === 'string' ? JSON.parse(configJson) : configJson;
      widgets.value = config.widgets || [];
    }
  } catch (e) {
    console.error('加载配置失败:', e);
  }
}

function getWidgetStyle(widget: any) {
  return {
    left: `${widget.x}px`,
    top: `${widget.y}px`,
    width: `${widget.width}px`,
    height: `${widget.height}px`,
    zIndex: widget.style?.zIndex || 1,
  };
}

function handleCommand(command: any) {
  console.log('预览模式 - 命令:', command);
  message.info(`命令: ${command.commandKey}, 参数: ${JSON.stringify(command.params)}`);
}

function handleHttpRequest(request: any) {
  console.log('预览模式 - HTTP请求:', request);
  message.info(`HTTP请求: ${request.config.method} ${request.config.url}`);
}

function handleApplyDevice() {
  message.success(`已应用测试设备ID: ${testDeviceId.value}`);
}

function resetMockData() {
  Object.assign(mockShadowState, {
    power: true,
    temperature: 25.5,
    humidity: 60,
    tempSet: 26,
    mode: 'cool',
    fanSpeed: 'auto',
    brightness: 80,
    online: true,
  });
}

function addMockField() {
  newField.key = '';
  newField.type = 'string';
  newField.value = '';
  addFieldVisible.value = true;
}

function confirmAddField() {
  if (!newField.key) {
    message.error('请输入字段名');
    return;
  }
  
  let value: any = newField.value;
  if (newField.type === 'number') {
    value = Number(newField.value) || 0;
  } else if (newField.type === 'boolean') {
    value = newField.value === 'true';
  }
  
  mockShadowState[newField.key] = value;
  addFieldVisible.value = false;
}

function handleBack() {
  // 如果是新窗口打开的预览，关闭窗口；否则返回上一页
  if (window.opener || isLocalPreview.value) {
    window.close();
  } else {
    router.back();
  }
}
</script>

<style lang="less" scoped>
.panel-preview-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #1a1a2e;
}

.preview-header {
  height: 50px;
  background: #16213e;
  border-bottom: 1px solid #0f3460;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  color: #fff;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
    
    .page-title {
      font-size: 14px;
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 8px;
  }
}

.preview-content {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  padding-right: 270px;
  overflow: auto;
}

.device-frame {
  background: #000;
  border-radius: 40px;
  padding: 12px;
  box-shadow: 0 0 40px rgba(0, 0, 0, 0.5);
  
  &.mobile {
    width: 414px;
    .device-screen {
      border-radius: 30px;
    }
  }
  
  &.tablet {
    width: 520px;
    border-radius: 30px;
    .device-screen {
      border-radius: 20px;
    }
  }
  
  &.desktop {
    width: 700px;
    border-radius: 16px;
    padding: 8px;
    .device-screen {
      border-radius: 8px;
    }
  }
  
  .device-screen {
    background: #fff;
    overflow: hidden;
    display: flex;
    align-items: flex-start;
    justify-content: center;
    padding-top: 20px;
  }
}

.preview-canvas {
  background: #fff;
  position: relative;
}

.preview-widget {
  position: absolute;
}

.mock-panel {
  position: fixed;
  right: 0;
  top: 50px;
  bottom: 0;
  width: 250px;
  background: #16213e;
  border-left: 1px solid #0f3460;
  display: flex;
  flex-direction: column;
  
  .mock-header {
    padding: 12px 16px;
    border-bottom: 1px solid #0f3460;
    display: flex;
    justify-content: space-between;
    align-items: center;
    color: #fff;
    font-weight: 500;
  }
  
  .mock-content {
    flex: 1;
    padding: 12px;
    overflow-y: auto;
    
    :deep(.ant-form-item) {
      margin-bottom: 8px;
    }
    
    :deep(.ant-form-item-label > label) {
      color: #ccc;
      font-size: 12px;
    }
  }
}
</style>
