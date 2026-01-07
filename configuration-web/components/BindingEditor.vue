<template>
  <div class="binding-editor">
    <a-alert 
      message="数据绑定说明" 
      description="设计时配置点位名称，运行时通过URL参数传入设备ID进行绑定"
      type="info" 
      show-icon 
      style="margin-bottom: 16px"
    />
    
    <a-collapse v-model:activeKey="activeKeys" :bordered="false">
      <!-- 简单绑定模式（单点位） -->
      <a-collapse-panel 
        v-if="definition?.binding?.supportDataBinding" 
        key="simple" 
        header="数据点位绑定"
      >
        <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
          <a-form-item label="点位名称">
            <a-input 
              v-model:value="widget.binding.point" 
              placeholder="如: power, temperature"
              @change="emitUpdate"
            />
          </a-form-item>
          <a-form-item label="数据类型">
            <a-select v-model:value="widget.binding.dataType" style="width: 100%" @change="emitUpdate">
              <a-select-option value="boolean">布尔值</a-select-option>
              <a-select-option value="number">数值</a-select-option>
              <a-select-option value="string">字符串</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item label="默认值">
            <a-input 
              v-model:value="widget.binding.defaultValue" 
              placeholder="无数据时显示的默认值"
              @change="emitUpdate"
            />
          </a-form-item>
          
          <!-- 单点位的数据转换 -->
          <a-divider style="margin: 12px 0">数据转换</a-divider>
          <a-form-item label="启用转换">
            <a-switch v-model:checked="widget.binding.enableTransform" @change="emitUpdate" />
          </a-form-item>
          <template v-if="widget.binding.enableTransform">
            <a-form-item label="转换类型">
              <a-select v-model:value="widget.binding.transformType" style="width: 100%" @change="emitUpdate">
                <a-select-option value="mapping">值映射</a-select-option>
                <a-select-option value="formula">公式计算</a-select-option>
                <a-select-option value="format">格式化</a-select-option>
              </a-select>
            </a-form-item>
            <template v-if="widget.binding.transformType === 'mapping'">
              <a-form-item label="映射规则">
                <a-button size="small" @click="openMappingEditor('single')">
                  编辑映射 ({{ Object.keys(widget.binding.mapping || {}).length }}条)
                </a-button>
              </a-form-item>
            </template>
            <template v-if="widget.binding.transformType === 'formula'">
              <a-form-item label="计算公式">
                <a-input 
                  v-model:value="widget.binding.formula" 
                  placeholder="如: value * 10 + 5"
                  @change="emitUpdate"
                />
              </a-form-item>
            </template>
            <template v-if="widget.binding.transformType === 'format'">
              <a-form-item label="格式模板">
                <a-input 
                  v-model:value="widget.binding.formatTemplate" 
                  placeholder="如: {value}°C"
                  @change="emitUpdate"
                />
              </a-form-item>
            </template>
          </template>
        </a-form>
      </a-collapse-panel>
      
      <!-- 多点位绑定模式（每个点位单独配置转换） -->
      <a-collapse-panel 
        v-if="definition?.binding?.points" 
        key="multi" 
        header="多点位绑定"
      >
        <div class="multi-point-list">
          <div 
            v-for="point in definition.binding.points" 
            :key="point.key"
            class="point-item"
          >
            <div class="point-header" @click="togglePointExpand(point.key)">
              <span class="point-label">{{ point.label }}</span>
              <span class="point-value">{{ widget.binding.points[point.key]?.field || '未绑定' }}</span>
              <DownOutlined :class="{ 'expanded': expandedPoints.includes(point.key) }" />
            </div>
            
            <div v-show="expandedPoints.includes(point.key)" class="point-config">
              <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
                <a-form-item label="绑定字段">
                  <a-input 
                    :value="getPointField(point.key)"
                    @change="(e) => setPointField(point.key, e.target.value)"
                    :placeholder="`如: ${point.key}`"
                  />
                </a-form-item>
                <a-form-item label="数据类型">
                  <a-select 
                    :value="getPointConfig(point.key, 'dataType') || 'string'" 
                    style="width: 100%" 
                    @change="(val) => setPointConfig(point.key, 'dataType', val)"
                  >
                    <a-select-option value="boolean">布尔值</a-select-option>
                    <a-select-option value="number">数值</a-select-option>
                    <a-select-option value="string">字符串</a-select-option>
                  </a-select>
                </a-form-item>
                <a-form-item label="默认值">
                  <a-input 
                    :value="getPointConfig(point.key, 'defaultValue')"
                    @change="(e) => setPointConfig(point.key, 'defaultValue', e.target.value)"
                    placeholder="无数据时的默认值"
                  />
                </a-form-item>
                
                <a-divider style="margin: 8px 0">数据转换</a-divider>
                
                <a-form-item label="启用转换">
                  <a-switch 
                    :checked="getPointConfig(point.key, 'enableTransform')" 
                    @change="(val) => setPointConfig(point.key, 'enableTransform', val)" 
                  />
                </a-form-item>
                
                <template v-if="getPointConfig(point.key, 'enableTransform')">
                  <a-form-item label="转换类型">
                    <a-select 
                      :value="getPointConfig(point.key, 'transformType') || 'mapping'" 
                      style="width: 100%" 
                      @change="(val) => setPointConfig(point.key, 'transformType', val)"
                    >
                      <a-select-option value="mapping">值映射</a-select-option>
                      <a-select-option value="formula">公式计算</a-select-option>
                      <a-select-option value="format">格式化</a-select-option>
                    </a-select>
                  </a-form-item>
                  
                  <!-- 值映射 -->
                  <template v-if="getPointConfig(point.key, 'transformType') === 'mapping'">
                    <a-form-item label="映射规则">
                      <a-button size="small" @click="openMappingEditor(point.key)">
                        编辑映射 ({{ Object.keys(getPointConfig(point.key, 'mapping') || {}).length }}条)
                      </a-button>
                    </a-form-item>
                  </template>
                  
                  <!-- 公式计算 -->
                  <template v-if="getPointConfig(point.key, 'transformType') === 'formula'">
                    <a-form-item label="计算公式">
                      <a-input 
                        :value="getPointConfig(point.key, 'formula')"
                        @change="(e) => setPointConfig(point.key, 'formula', e.target.value)"
                        placeholder="如: value * 10 + 5"
                      />
                    </a-form-item>
                  </template>
                  
                  <!-- 格式化 -->
                  <template v-if="getPointConfig(point.key, 'transformType') === 'format'">
                    <a-form-item label="格式模板">
                      <a-input 
                        :value="getPointConfig(point.key, 'formatTemplate')"
                        @change="(e) => setPointConfig(point.key, 'formatTemplate', e.target.value)"
                        placeholder="如: {value}°C"
                      />
                    </a-form-item>
                  </template>
                </template>
              </a-form>
            </div>
          </div>
        </div>
      </a-collapse-panel>
      
      <!-- 数据刷新 -->
      <a-collapse-panel key="refresh" header="数据刷新">
        <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
          <a-form-item label="刷新方式">
            <a-select v-model:value="widget.binding.refreshMode" style="width: 100%" @change="emitUpdate">
              <a-select-option value="websocket">WebSocket推送</a-select-option>
              <a-select-option value="polling">定时轮询</a-select-option>
              <a-select-option value="manual">手动刷新</a-select-option>
            </a-select>
          </a-form-item>
          <a-form-item v-if="widget.binding.refreshMode === 'polling'" label="轮询间隔(秒)">
            <a-input-number 
              v-model:value="widget.binding.pollingInterval" 
              :min="1" 
              :max="3600"
              style="width: 100%"
              @change="emitUpdate"
            />
          </a-form-item>
        </a-form>
      </a-collapse-panel>
    </a-collapse>
    
    <!-- 映射编辑弹窗 -->
    <a-modal v-model:open="mappingEditorVisible" title="编辑值映射" @ok="saveMappingEditor" width="500px">
      <div class="mapping-editor-header">
        <span>正在编辑: <strong>{{ currentEditingPointLabel }}</strong></span>
      </div>
      <a-table 
        :dataSource="mappingData" 
        :columns="mappingColumns"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'source'">
            <a-input v-model:value="record.source" size="small" placeholder="原始值" />
          </template>
          <template v-else-if="column.dataIndex === 'target'">
            <a-input v-model:value="record.target" size="small" placeholder="显示值" />
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-button type="link" danger size="small" @click="removeMappingItem(index)">删除</a-button>
          </template>
        </template>
      </a-table>
      <a-button type="dashed" block style="margin-top: 8px" @click="addMappingItem">
        + 添加映射
      </a-button>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, computed, onMounted } from 'vue';
import { DownOutlined } from '@ant-design/icons-vue';

const props = defineProps({
  widget: { type: Object, required: true },
  definition: { type: Object, default: null },
});

const emit = defineEmits(['update']);

const activeKeys = ref(['simple', 'multi']);
const expandedPoints = ref<string[]>([]);

// 初始化绑定配置
onMounted(() => {
  if (!props.widget.binding) {
    props.widget.binding = {};
  }
  if (!props.widget.binding.points) {
    props.widget.binding.points = {};
  }
  if (!props.widget.binding.refreshMode) {
    props.widget.binding.refreshMode = 'websocket';
  }
  if (!props.widget.binding.pollingInterval) {
    props.widget.binding.pollingInterval = 5;
  }
});

function emitUpdate() {
  emit('update', props.widget.binding);
}

// 切换点位展开/收起
function togglePointExpand(pointKey: string) {
  const index = expandedPoints.value.indexOf(pointKey);
  if (index > -1) {
    expandedPoints.value.splice(index, 1);
  } else {
    expandedPoints.value.push(pointKey);
  }
}

// 获取点位字段值（兼容旧数据格式）
function getPointField(pointKey: string): string {
  const pointData = props.widget.binding.points[pointKey];
  if (typeof pointData === 'string') {
    return pointData;
  }
  return pointData?.field || '';
}

// 设置点位字段值
function setPointField(pointKey: string, value: string) {
  ensurePointObject(pointKey);
  props.widget.binding.points[pointKey].field = value;
  emitUpdate();
}

// 获取点位配置项
function getPointConfig(pointKey: string, configKey: string): any {
  const pointData = props.widget.binding.points[pointKey];
  if (typeof pointData === 'object' && pointData !== null) {
    return pointData[configKey];
  }
  return undefined;
}

// 设置点位配置项
function setPointConfig(pointKey: string, configKey: string, value: any) {
  ensurePointObject(pointKey);
  props.widget.binding.points[pointKey][configKey] = value;
  emitUpdate();
}

// 确保点位数据是对象格式
function ensurePointObject(pointKey: string) {
  const pointData = props.widget.binding.points[pointKey];
  if (typeof pointData === 'string') {
    // 旧格式转新格式
    props.widget.binding.points[pointKey] = { field: pointData };
  } else if (!pointData) {
    props.widget.binding.points[pointKey] = {};
  }
}

// 映射编辑器
const mappingEditorVisible = ref(false);
const mappingData = ref<any[]>([]);
const currentEditingPoint = ref<string>('single');

const currentEditingPointLabel = computed(() => {
  if (currentEditingPoint.value === 'single') {
    return '单点位绑定';
  }
  const point = props.definition?.binding?.points?.find((p: any) => p.key === currentEditingPoint.value);
  return point?.label || currentEditingPoint.value;
});

const mappingColumns = [
  { title: '原始值', dataIndex: 'source', width: 150 },
  { title: '显示值', dataIndex: 'target', width: 150 },
  { title: '操作', dataIndex: 'action', width: 80 },
];

function openMappingEditor(pointKey: string) {
  currentEditingPoint.value = pointKey;
  let mapping: Record<string, string> = {};
  
  if (pointKey === 'single') {
    mapping = props.widget.binding.mapping || {};
  } else {
    mapping = getPointConfig(pointKey, 'mapping') || {};
  }
  
  mappingData.value = Object.entries(mapping).map(([source, target]) => ({ source, target }));
  if (mappingData.value.length === 0) {
    mappingData.value.push({ source: '', target: '' });
  }
  mappingEditorVisible.value = true;
}

function addMappingItem() {
  mappingData.value.push({ source: '', target: '' });
}

function removeMappingItem(index: number) {
  mappingData.value.splice(index, 1);
}

function saveMappingEditor() {
  const mapping: Record<string, string> = {};
  mappingData.value.forEach(item => {
    if (item.source !== '' && item.source !== null && item.source !== undefined) {
      mapping[item.source] = item.target;
    }
  });
  
  if (currentEditingPoint.value === 'single') {
    props.widget.binding.mapping = mapping;
  } else {
    setPointConfig(currentEditingPoint.value, 'mapping', mapping);
  }
  
  mappingEditorVisible.value = false;
  emitUpdate();
}
</script>

<style lang="less" scoped>
.binding-editor {
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
}

.multi-point-list {
  .point-item {
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    margin-bottom: 8px;
    overflow: hidden;
    
    &:last-child {
      margin-bottom: 0;
    }
  }
  
  .point-header {
    display: flex;
    align-items: center;
    padding: 8px 12px;
    background: #fafafa;
    cursor: pointer;
    
    &:hover {
      background: #f0f0f0;
    }
    
    .point-label {
      font-weight: 500;
      flex: 1;
    }
    
    .point-value {
      color: #999;
      margin-right: 8px;
      font-size: 12px;
    }
    
    .anticon {
      transition: transform 0.2s;
      
      &.expanded {
        transform: rotate(180deg);
      }
    }
  }
  
  .point-config {
    padding: 12px;
    border-top: 1px solid #e8e8e8;
    background: #fff;
  }
}

.mapping-editor-header {
  margin-bottom: 12px;
  padding: 8px 12px;
  background: #f5f5f5;
  border-radius: 4px;
  
  strong {
    color: #1890ff;
  }
}
</style>
