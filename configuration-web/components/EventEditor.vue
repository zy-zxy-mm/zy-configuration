<template>
  <div class="event-editor">
    <a-alert 
      message="事件绑定说明" 
      description="配置组件交互事件，支持HTTP/HTTPS请求调用外部接口"
      type="info" 
      show-icon 
      style="margin-bottom: 16px"
    />
    
    <template v-if="definition?.events?.length">
      <a-collapse v-model:activeKey="activeKeys" :bordered="false">
        <a-collapse-panel 
          v-for="event in definition.events" 
          :key="event.key"
          :header="event.name"
        >
          <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }" size="small">
            <a-form-item label="启用">
              <a-switch 
                v-model:checked="widget.events[event.key].enabled" 
                @change="emitUpdate"
              />
            </a-form-item>
            
            <template v-if="widget.events[event.key].enabled">
              <a-form-item label="请求方式">
                <a-select 
                  v-model:value="widget.events[event.key].method" 
                  style="width: 100%"
                  @change="emitUpdate"
                >
                  <a-select-option value="GET">GET</a-select-option>
                  <a-select-option value="POST">POST</a-select-option>
                  <a-select-option value="PUT">PUT</a-select-option>
                  <a-select-option value="DELETE">DELETE</a-select-option>
                </a-select>
              </a-form-item>
              
              <a-form-item label="请求地址">
                <a-input 
                  v-model:value="widget.events[event.key].url" 
                  placeholder="http(s)://..."
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-form-item label="请求头">
                <a-button size="small" @click="openHeadersEditor(event.key)">
                  编辑请求头 ({{ Object.keys(widget.events[event.key].headers || {}).length }}项)
                </a-button>
              </a-form-item>
              
              <a-form-item v-if="widget.events[event.key].method !== 'GET'" label="请求体">
                <a-textarea 
                  v-model:value="widget.events[event.key].bodyTemplate" 
                  placeholder='{"value": "${value}", "deviceId": "${deviceId}"}'
                  :rows="4"
                  @change="emitUpdate"
                />
                <div class="help-text">
                  支持变量: ${value}, ${deviceId}, ${timestamp}
                  <template v-if="event.params?.length">
                    , {{ event.params.map(p => '${' + p + '}').join(', ') }}
                  </template>
                </div>
              </a-form-item>
              
              <a-form-item label="成功提示">
                <a-input 
                  v-model:value="widget.events[event.key].successMessage" 
                  placeholder="操作成功"
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-form-item label="失败提示">
                <a-input 
                  v-model:value="widget.events[event.key].errorMessage" 
                  placeholder="操作失败"
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-divider style="margin: 12px 0" />
              
              <a-form-item label="确认弹窗">
                <a-switch 
                  v-model:checked="widget.events[event.key].confirmEnabled" 
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-form-item v-if="widget.events[event.key].confirmEnabled" label="确认文字">
                <a-input 
                  v-model:value="widget.events[event.key].confirmText" 
                  placeholder="确定执行此操作吗？"
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-divider style="margin: 12px 0" />
              
              <a-form-item label="防抖延迟(ms)">
                <a-input-number 
                  v-model:value="widget.events[event.key].debounceDelay" 
                  :min="0" 
                  :max="5000"
                  style="width: 100%"
                  @change="emitUpdate"
                />
              </a-form-item>
              
              <a-form-item label="超时时间(ms)">
                <a-input-number 
                  v-model:value="widget.events[event.key].timeout" 
                  :min="1000" 
                  :max="60000"
                  style="width: 100%"
                  @change="emitUpdate"
                />
              </a-form-item>
            </template>
          </a-form>
        </a-collapse-panel>
      </a-collapse>
    </template>
    
    <a-empty v-else description="该组件暂无可配置事件" />
    
    <!-- 请求头编辑弹窗 -->
    <a-modal v-model:open="headersEditorVisible" title="编辑请求头" @ok="saveHeadersEditor">
      <a-table 
        :dataSource="headersData" 
        :columns="headersColumns"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'key'">
            <a-input v-model:value="record.key" size="small" placeholder="Header名称" />
          </template>
          <template v-else-if="column.dataIndex === 'value'">
            <a-input v-model:value="record.value" size="small" placeholder="Header值" />
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-button type="link" danger size="small" @click="removeHeaderItem(index)">删除</a-button>
          </template>
        </template>
      </a-table>
      <a-button type="dashed" block style="margin-top: 8px" @click="addHeaderItem">
        + 添加请求头
      </a-button>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch, onMounted } from 'vue';

const props = defineProps({
  widget: { type: Object, required: true },
  definition: { type: Object, default: null },
});

const emit = defineEmits(['update']);

const activeKeys = ref<string[]>([]);

// 初始化事件配置
onMounted(() => {
  if (!props.widget.events) {
    props.widget.events = {};
  }
  
  // 为每个事件初始化默认配置
  if (props.definition?.events) {
    props.definition.events.forEach((event: any) => {
      if (!props.widget.events[event.key]) {
        props.widget.events[event.key] = {
          enabled: false,
          method: 'POST',
          url: '',
          headers: { 'Content-Type': 'application/json' },
          bodyTemplate: '{"value": "${value}"}',
          successMessage: '操作成功',
          errorMessage: '操作失败',
          confirmEnabled: false,
          confirmText: '确定执行此操作吗？',
          debounceDelay: 300,
          timeout: 10000,
        };
      }
    });
    // 默认展开第一个事件
    if (props.definition.events.length > 0) {
      activeKeys.value = [props.definition.events[0].key];
    }
  }
});

function emitUpdate() {
  emit('update', props.widget.events);
}

// 请求头编辑器
const headersEditorVisible = ref(false);
const currentEventKey = ref('');
const headersData = ref<any[]>([]);
const headersColumns = [
  { title: '名称', dataIndex: 'key', width: 150 },
  { title: '值', dataIndex: 'value', width: 200 },
  { title: '操作', dataIndex: 'action', width: 80 },
];

function openHeadersEditor(eventKey: string) {
  currentEventKey.value = eventKey;
  const headers = props.widget.events[eventKey].headers || {};
  headersData.value = Object.entries(headers).map(([key, value]) => ({ key, value }));
  headersEditorVisible.value = true;
}

function addHeaderItem() {
  headersData.value.push({ key: '', value: '' });
}

function removeHeaderItem(index: number) {
  headersData.value.splice(index, 1);
}

function saveHeadersEditor() {
  const headers: Record<string, string> = {};
  headersData.value.forEach(item => {
    if (item.key) {
      headers[item.key] = item.value;
    }
  });
  props.widget.events[currentEventKey.value].headers = headers;
  headersEditorVisible.value = false;
  emitUpdate();
}
</script>

<style lang="less" scoped>
.event-editor {
  :deep(.ant-collapse-header) {
    padding: 8px 12px !important;
    font-weight: 500;
  }
  
  :deep(.ant-collapse-content-box) {
    padding: 8px 12px !important;
  }
  
  :deep(.ant-form-item) {
    margin-bottom: 8px;
  }
  
  .help-text {
    font-size: 12px;
    color: #999;
    margin-top: 4px;
  }
}
</style>
