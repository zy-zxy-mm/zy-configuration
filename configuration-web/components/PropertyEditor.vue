<template>
  <div class="property-editor">
    <!-- 基础属性 -->
    <a-collapse v-model:activeKey="activeKeys" :bordered="false">
      <a-collapse-panel key="position" header="位置尺寸">
        <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
          <a-form-item label="X坐标">
            <a-input-number v-model:value="widget.x" :min="0" style="width: 100%" @change="emitUpdate" />
          </a-form-item>
          <a-form-item label="Y坐标">
            <a-input-number v-model:value="widget.y" :min="0" style="width: 100%" @change="emitUpdate" />
          </a-form-item>
          <a-form-item label="宽度">
            <a-input-number v-model:value="widget.width" :min="20" style="width: 100%" @change="emitUpdate" />
          </a-form-item>
          <a-form-item label="高度">
            <a-input-number v-model:value="widget.height" :min="20" style="width: 100%" @change="emitUpdate" />
          </a-form-item>
        </a-form>
      </a-collapse-panel>
      
      <a-collapse-panel key="props" header="组件属性" v-if="definition?.props">
        <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
          <template v-for="(config, key) in definition.props" :key="key">
            <!-- 字符串输入 -->
            <a-form-item v-if="config.type === 'string'" :label="config.label">
              <a-input 
                v-model:value="widget.props[key]" 
                :placeholder="`请输入${config.label}`"
                @change="emitUpdate"
              />
            </a-form-item>
            
            <!-- 数字输入 -->
            <a-form-item v-else-if="config.type === 'number'" :label="config.label">
              <a-input-number 
                v-model:value="widget.props[key]" 
                :min="config.min"
                :max="config.max"
                style="width: 100%"
                @change="emitUpdate"
              />
            </a-form-item>
            
            <!-- 布尔开关 -->
            <a-form-item v-else-if="config.type === 'boolean'" :label="config.label">
              <a-switch v-model:checked="widget.props[key]" @change="emitUpdate" />
            </a-form-item>
            
            <!-- 下拉选择 -->
            <a-form-item v-else-if="config.type === 'select'" :label="config.label">
              <a-select 
                v-model:value="widget.props[key]" 
                style="width: 100%"
                @change="emitUpdate"
              >
                <a-select-option v-for="opt in config.options" :key="opt" :value="opt">
                  {{ opt }}
                </a-select-option>
              </a-select>
            </a-form-item>
            
            <!-- 颜色选择 -->
            <a-form-item v-else-if="config.type === 'color'" :label="config.label">
              <div class="color-picker-wrapper">
                <input 
                  type="color" 
                  v-model="widget.props[key]" 
                  class="color-input"
                  @change="emitUpdate"
                />
                <a-input 
                  v-model:value="widget.props[key]" 
                  style="flex: 1"
                  @change="emitUpdate"
                />
              </div>
            </a-form-item>
            
            <!-- 数组编辑（选项列表） -->
            <a-form-item v-else-if="config.type === 'array'" :label="config.label">
              <a-button size="small" @click="openArrayEditor(key, config)">
                编辑列表 ({{ widget.props[key]?.length || 0 }}项)
              </a-button>
            </a-form-item>
          </template>
        </a-form>
      </a-collapse-panel>
      
      <a-collapse-panel key="style" header="样式设置">
        <a-form :label-col="{ span: 8 }" :wrapper-col="{ span: 16 }" size="small">
          <a-form-item label="层级">
            <a-input-number 
              v-model:value="widget.style.zIndex" 
              :min="1" 
              :max="999"
              style="width: 100%"
              @change="emitUpdate"
            />
          </a-form-item>
        </a-form>
      </a-collapse-panel>
    </a-collapse>
    
    <!-- 数组编辑弹窗 -->
    <a-modal 
      v-model:open="arrayEditorVisible" 
      :title="`编辑${currentArrayConfig?.label || '列表'}`"
      width="600px"
      @ok="saveArrayEditor"
    >
      <a-table 
        :dataSource="arrayEditorData" 
        :columns="arrayEditorColumns"
        :pagination="false"
        size="small"
      >
        <template #bodyCell="{ column, record, index }">
          <template v-if="column.dataIndex === 'label' || column.dataIndex === 'name'">
            <a-input v-model:value="record[column.dataIndex]" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'value' || column.dataIndex === 'key'">
            <a-input v-model:value="record[column.dataIndex]" size="small" />
          </template>
          <template v-else-if="column.dataIndex === 'action'">
            <a-button type="link" danger size="small" @click="removeArrayItem(index)">删除</a-button>
          </template>
        </template>
      </a-table>
      <a-button type="dashed" block style="margin-top: 8px" @click="addArrayItem">
        + 添加选项
      </a-button>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref, watch } from 'vue';

const props = defineProps({
  widget: { type: Object, required: true },
  definition: { type: Object, default: null },
});

const emit = defineEmits(['update']);

const activeKeys = ref(['position', 'props', 'style']);

// 数组编辑器
const arrayEditorVisible = ref(false);
const arrayEditorKey = ref('');
const currentArrayConfig = ref<any>(null);
const arrayEditorData = ref<any[]>([]);
const arrayEditorColumns = ref<any[]>([]);

function emitUpdate() {
  emit('update', props.widget.props);
}

function openArrayEditor(key: string, config: any) {
  arrayEditorKey.value = key;
  currentArrayConfig.value = config;
  arrayEditorData.value = JSON.parse(JSON.stringify(props.widget.props[key] || []));
  
  // 根据数据结构生成列
  if (arrayEditorData.value.length > 0) {
    const sample = arrayEditorData.value[0];
    arrayEditorColumns.value = Object.keys(sample)
      .filter(k => k !== 'icon')
      .map(k => ({
        title: k === 'label' ? '显示文本' : k === 'value' ? '值' : k === 'name' ? '名称' : k === 'key' ? '键' : k,
        dataIndex: k,
        width: 150,
      }));
    arrayEditorColumns.value.push({ title: '操作', dataIndex: 'action', width: 80 });
  } else {
    arrayEditorColumns.value = [
      { title: '显示文本', dataIndex: 'label', width: 150 },
      { title: '值', dataIndex: 'value', width: 150 },
      { title: '操作', dataIndex: 'action', width: 80 },
    ];
  }
  
  arrayEditorVisible.value = true;
}

function addArrayItem() {
  const sample = arrayEditorData.value[0];
  if (sample) {
    const newItem: any = {};
    Object.keys(sample).forEach(k => {
      newItem[k] = '';
    });
    arrayEditorData.value.push(newItem);
  } else {
    arrayEditorData.value.push({ label: '', value: '' });
  }
}

function removeArrayItem(index: number) {
  arrayEditorData.value.splice(index, 1);
}

function saveArrayEditor() {
  props.widget.props[arrayEditorKey.value] = arrayEditorData.value;
  arrayEditorVisible.value = false;
  emitUpdate();
}
</script>

<style lang="less" scoped>
.property-editor {
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
  
  .color-picker-wrapper {
    display: flex;
    gap: 8px;
    align-items: center;
    
    .color-input {
      width: 32px;
      height: 32px;
      padding: 0;
      border: 1px solid #d9d9d9;
      border-radius: 4px;
      cursor: pointer;
    }
  }
}
</style>
