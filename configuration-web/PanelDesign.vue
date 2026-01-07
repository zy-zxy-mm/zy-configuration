<template>
  <div class="panel-design-container">
    <!-- 顶部工具栏 -->
    <div class="design-toolbar">
      <div class="toolbar-left">
        <a-button @click="handleBack">
          <template #icon>
            <component :is="Icons.LeftOutlined"/>
          </template>
          返回
        </a-button>
        <span class="page-title">{{ pageInfo?.pageName || '设计器' }}</span>
      </div>
      <div class="toolbar-center">
        <a-button-group>
          <a-tooltip title="撤销">
            <a-button @click="handleUndo" :disabled="!canUndo">
              <UndoOutlined/>
            </a-button>
          </a-tooltip>

          <a-tooltip title="重做">
            <a-button @click="handleRedo" :disabled="!canRedo">
              <RedoOutlined/>
            </a-button>
          </a-tooltip>
        </a-button-group>

        <a-divider type="vertical"/>

        <a-tooltip title="复制">
          <a-button @click="handleCopy" :disabled="!selectedWidget">
            <CopyOutlined/>
          </a-button>
        </a-tooltip>

        <a-tooltip title="粘贴">
          <a-button @click="handlePaste" :disabled="!clipboard">
            <SnippetsOutlined/>
          </a-button>
        </a-tooltip>

        <a-tooltip title="删除">
          <a-button @click="handleDelete" :disabled="!selectedWidget" danger>
            <DeleteOutlined/>
          </a-button>
        </a-tooltip>

        <a-divider type="vertical"/>

        <span class="zoom-control">
          <a-tooltip title="缩小">
            <a-button size="small" @click="handleZoom(-0.1)">
              <ZoomOutOutlined/>
            </a-button>
          </a-tooltip>

          <span class="zoom-value">{{ Math.round(zoom * 100) }}%</span>

          <a-tooltip title="放大">
            <a-button size="small" @click="handleZoom(0.1)">
              <ZoomInOutlined/>
            </a-button>
          </a-tooltip>
        </span>
      </div>

      <div class="toolbar-right">
        <a-button @click="handlePreview">预览</a-button>
        <a-button @click="handleSave" :loading="saving">保存</a-button>
        <a-button type="primary" @click="handleSaveAndPublish" :loading="publishing">保存并发布
        </a-button>
      </div>
    </div>

    <div class="design-content">
      <!-- 左侧组件库 -->
      <div class="widget-panel">
        <div class="panel-header">
          <span>组件库</span>
          <a-input-search v-model:value="searchKeyword" placeholder="搜索" size="small"
                          style="width: 100px"/>
        </div>
        <a-collapse v-model:activeKey="activeCategoryKeys" :bordered="false"
                    expandIconPosition="end">
          <a-collapse-panel v-for="category in widgetCategories" :key="category.key"
                            :header="category.name">
            <div class="widget-grid">
              <div
                v-for="widget in getFilteredWidgets(category.key)"
                :key="widget.type"
                class="widget-item"
                draggable="true"
                @dragstart="handleDragStart($event, widget)"
              >
                <div class="widget-icon">
                  <component :is="getIconComponent(widget.icon)"/>
                </div>
                <span class="widget-name">{{ widget.name }}</span>
              </div>
            </div>
          </a-collapse-panel>
        </a-collapse>
      </div>

      <!-- 中间画布 -->
      <div class="canvas-panel" ref="canvasPanelRef">
        <div class="canvas-wrapper" :style="canvasWrapperStyle">
          <div
            class="canvas"
            :style="canvasStyle"
            @drop="handleDrop"
            @dragover.prevent
            @click="handleCanvasClick"
          >
            <div class="canvas-grid"></div>
            <div
              v-for="item in widgets"
              :key="item.id"
              class="widget-wrapper"
              :class="{ selected: selectedWidget?.id === item.id }"
              :style="getWidgetStyle(item)"
              @mousedown="handleWidgetMouseDown($event, item)"
              @click.stop="handleWidgetClick(item)"
            >
              <WidgetRenderer :widget="item" :isDesign="true"/>
              <template v-if="selectedWidget?.id === item.id">
                <div class="resize-handle se"
                     @mousedown.stop="handleResizeStart($event, item, 'se')"></div>
              </template>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧属性面板 -->
      <div class="property-panel">
        <a-tabs v-model:activeKey="propertyTab" size="small">
          <a-tab-pane key="props" tab="属性">
            <div v-if="selectedWidget" class="property-content">
              <PropertyEditor :widget="selectedWidget" :definition="selectedWidgetDefinition"
                              @update="handlePropertyUpdate"/>
            </div>
            <div v-else class="no-selection">
              <component :is="Icons.AppstoreOutlined" style="font-size: 48px; color: #d9d9d9"/>
              <p>请选择组件</p>
            </div>
          </a-tab-pane>
          <a-tab-pane key="bindingTab" tab="绑定">
            <div v-if="selectedWidget" class="property-content">
              <BindingEditor :widget="selectedWidget" :definition="selectedWidgetDefinition"
                             @update="handleBindingUpdate"/>
            </div>
            <div v-else class="no-selection">
              <component :is="Icons.LinkOutlined" style="font-size: 48px; color: #d9d9d9"/>
              <p>请选择组件</p>
            </div>
          </a-tab-pane>
          <a-tab-pane key="events" tab="事件">
            <div v-if="selectedWidget" class="property-content">
              <EventEditor :widget="selectedWidget" :definition="selectedWidgetDefinition"
                           @update="handleEventUpdate"/>
            </div>
            <div v-else class="no-selection">
              <component :is="Icons.ThunderboltOutlined" style="font-size: 48px; color: #d9d9d9"/>
              <p>请选择组件</p>
            </div>
          </a-tab-pane>
        </a-tabs>
      </div>
    </div>

    <a-modal v-model:open="saveModalVisible" title="保存版本" @ok="confirmSave">
      <a-form :label-col="{ span: 6 }" :wrapper-col="{ span: 18 }">
        <a-form-item label="变更说明">
          <a-textarea v-model:value="changeLog" placeholder="请输入变更说明" :rows="3"/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import {ref, computed, onMounted, markRaw} from 'vue';
import {useRoute, useRouter} from 'vue-router';
import {message} from 'ant-design-vue';
import * as Icons from '@ant-design/icons-vue';
import {getPageById, getPublishedConfig, saveVersion, publishVersion} from './api/panel.api';
import {
  widgetCategories,
  widgetDefinitions,
  getWidgetDefinition,
  createWidgetInstance
} from './data/widgetConfig';
import WidgetRenderer from './components/WidgetRenderer.vue';
import PropertyEditor from './components/PropertyEditor.vue';
import BindingEditor from './components/BindingEditor.vue';
import EventEditor from './components/EventEditor.vue';
import {
  UndoOutlined,
  RedoOutlined,
  CopyOutlined,
  SnippetsOutlined,
  DeleteOutlined,
  ZoomInOutlined,
  ZoomOutOutlined,
} from '@ant-design/icons-vue'

defineOptions({name: 'ConfigurationPanelDesign'});

const {
  LeftOutlined, UndoOutlined, RedoOutlined, CopyOutlined, SnippetsOutlined,
  DeleteOutlined, AppstoreOutlined, LinkOutlined, ThunderboltOutlined,
  ZoomInOutlined, ZoomOutOutlined,
} = Icons;

const route = useRoute();
const router = useRouter();

function getIconComponent(iconName: string) {
  return (Icons as any)[iconName] || Icons.AppstoreOutlined;
}

const pageId = ref(route.query.pageId as string);
const pageInfo = ref<any>(null);
const widgets = ref<any[]>([]);
const selectedWidget = ref<any>(null);
const clipboard = ref<any>(null);
const zoom = ref(1);
const canvasPanelRef = ref<HTMLElement | null>(null);

const history = ref<string[]>([]);
const historyIndex = ref(-1);
const canUndo = computed(() => historyIndex.value > 0);
const canRedo = computed(() => historyIndex.value < history.value.length - 1);

const searchKeyword = ref('');
const activeCategoryKeys = ref(['basic', 'lighting', 'hvac', 'security', 'sensor', 'display', 'input']);
const propertyTab = ref('props');
const saving = ref(false);
const publishing = ref(false);
const saveModalVisible = ref(false);
const changeLog = ref('');
const isPublishAfterSave = ref(false);

const selectedWidgetDefinition = computed(() => {
  if (!selectedWidget.value) return null;
  return getWidgetDefinition(selectedWidget.value.type);
});

const canvasStyle = computed(() => ({
  width: `${pageInfo.value?.canvasWidth || 390}px`,
  height: `${pageInfo.value?.canvasHeight || 844}px`,
  transform: `scale(${zoom.value})`,
  transformOrigin: 'center center',
}));

const canvasWrapperStyle = computed(() => ({
  width: `${(pageInfo.value?.canvasWidth || 390) * zoom.value + 100}px`,
  height: `${(pageInfo.value?.canvasHeight || 844) * zoom.value + 100}px`,
}));

function getFilteredWidgets(category: string) {
  let list = widgetDefinitions.filter(w => w.category === category);
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase();
    list = list.filter(w => w.name.toLowerCase().includes(keyword) || w.type.toLowerCase().includes(keyword));
  }
  return list;
}

onMounted(async () => {
  await loadPageInfo();
  await loadConfig();
  saveHistory();
});

async function loadPageInfo() {
  if (pageId.value) {
    const res = await getPageById(pageId.value);
    pageInfo.value = res;
  }
}

async function loadConfig() {
  if (pageId.value) {
    try {
      const res = await getPublishedConfig(pageId.value);
      if (res) {
        const config = typeof res === 'string' ? JSON.parse(res) : res;
        widgets.value = config.widgets || [];
      }
    } catch (e) {
      widgets.value = [];
    }
  }
}

function saveHistory() {
  const state = JSON.stringify(widgets.value);
  if (historyIndex.value < history.value.length - 1) {
    history.value = history.value.slice(0, historyIndex.value + 1);
  }
  history.value.push(state);
  historyIndex.value = history.value.length - 1;
  if (history.value.length > 50) {
    history.value.shift();
    historyIndex.value--;
  }
}

function handleUndo() {
  if (!canUndo.value) return;
  historyIndex.value--;
  widgets.value = JSON.parse(history.value[historyIndex.value]);
  selectedWidget.value = null;
}

function handleRedo() {
  if (!canRedo.value) return;
  historyIndex.value++;
  widgets.value = JSON.parse(history.value[historyIndex.value]);
  selectedWidget.value = null;
}

function handleCopy() {
  if (!selectedWidget.value) return;
  clipboard.value = JSON.parse(JSON.stringify(selectedWidget.value));
  message.success('已复制');
}

function handlePaste() {
  if (!clipboard.value) return;
  const newWidget = {
    ...clipboard.value,
    id: generateId(),
    x: clipboard.value.x + 20,
    y: clipboard.value.y + 20
  };
  widgets.value.push(newWidget);
  selectedWidget.value = newWidget;
  saveHistory();
}

function handleDelete() {
  if (!selectedWidget.value) return;
  const index = widgets.value.findIndex(w => w.id === selectedWidget.value.id);
  if (index > -1) {
    widgets.value.splice(index, 1);
    selectedWidget.value = null;
    saveHistory();
  }
}

function handleZoom(delta: number) {
  zoom.value = Math.max(0.25, Math.min(2, zoom.value + delta));
}

let dragWidget: any = null;

function handleDragStart(e: DragEvent, widget: any) {
  dragWidget = widget;
  e.dataTransfer?.setData('text/plain', widget.type);
}

function handleDrop(e: DragEvent) {
  e.preventDefault();
  if (!dragWidget) return;
  const canvas = e.currentTarget as HTMLElement;
  const rect = canvas.getBoundingClientRect();
  let x = (e.clientX - rect.left) / zoom.value;
  let y = (e.clientY - rect.top) / zoom.value;
  x = Math.round(x / 10) * 10;
  y = Math.round(y / 10) * 10;
  const newWidget = createWidgetInstance(dragWidget.type, x, y);
  if (newWidget) {
    widgets.value.push(newWidget);
    selectedWidget.value = newWidget;
    saveHistory();
  }
  dragWidget = null;
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

function handleCanvasClick() {
  selectedWidget.value = null;
}

function handleWidgetClick(widget: any) {
  selectedWidget.value = widget;
}

let isDragging = false;
let isResizing = false;

function handleWidgetMouseDown(e: MouseEvent, widget: any) {
  if (isResizing) return;
  isDragging = true;
  selectedWidget.value = widget;
  const startX = e.clientX;
  const startY = e.clientY;
  const startWidgetX = widget.x;
  const startWidgetY = widget.y;

  const handleMouseMove = (moveEvent: MouseEvent) => {
    if (!isDragging) return;
    let newX = startWidgetX + (moveEvent.clientX - startX) / zoom.value;
    let newY = startWidgetY + (moveEvent.clientY - startY) / zoom.value;
    newX = Math.round(newX / 10) * 10;
    newY = Math.round(newY / 10) * 10;
    newX = Math.max(0, Math.min(newX, (pageInfo.value?.canvasWidth || 390) - widget.width));
    newY = Math.max(0, Math.min(newY, (pageInfo.value?.canvasHeight || 844) - widget.height));
    widget.x = newX;
    widget.y = newY;
  };

  const handleMouseUp = () => {
    if (isDragging) saveHistory();
    isDragging = false;
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  };

  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
}

function handleResizeStart(e: MouseEvent, widget: any, direction: string) {
  e.stopPropagation();
  isResizing = true;
  const startX = e.clientX;
  const startY = e.clientY;
  const startWidth = widget.width;
  const startHeight = widget.height;

  const handleMouseMove = (moveEvent: MouseEvent) => {
    if (!isResizing) return;
    let newWidth = startWidth + (moveEvent.clientX - startX) / zoom.value;
    let newHeight = startHeight + (moveEvent.clientY - startY) / zoom.value;
    newWidth = Math.round(newWidth / 10) * 10;
    newHeight = Math.round(newHeight / 10) * 10;
    newWidth = Math.max(20, newWidth);
    newHeight = Math.max(20, newHeight);
    widget.width = newWidth;
    widget.height = newHeight;
  };

  const handleMouseUp = () => {
    if (isResizing) saveHistory();
    isResizing = false;
    document.removeEventListener('mousemove', handleMouseMove);
    document.removeEventListener('mouseup', handleMouseUp);
  };

  document.addEventListener('mousemove', handleMouseMove);
  document.addEventListener('mouseup', handleMouseUp);
}

function handlePropertyUpdate(props: any) {
  if (selectedWidget.value) {
    selectedWidget.value.props = {...selectedWidget.value.props, ...props};
    saveHistory();
  }
}

function handleBindingUpdate(bindingData: any) {
  if (selectedWidget.value) {
    selectedWidget.value.binding = {...selectedWidget.value.binding, ...bindingData};
    saveHistory();
  }
}

function handleEventUpdate(events: any) {
  if (selectedWidget.value) {
    selectedWidget.value.events = {...selectedWidget.value.events, ...events};
    saveHistory();
  }
}

function handleBack() {
  router.back();
}

function handlePreview() {
  localStorage.setItem('panel_preview_config', JSON.stringify({
    widgets: widgets.value,
    canvasWidth: pageInfo.value?.canvasWidth,
    canvasHeight: pageInfo.value?.canvasHeight,
  }));
  window.open(`/smartPark/configuration/PanelPreview?pageId=${pageId.value}&preview=local`, '_blank');
}

function handleSave() {
  isPublishAfterSave.value = false;
  saveModalVisible.value = true;
}

function handleSaveAndPublish() {
  isPublishAfterSave.value = true;
  saveModalVisible.value = true;
}

async function confirmSave() {
  try {
    saving.value = true;
    const configJson = JSON.stringify({
      widgets: widgets.value,
      canvasWidth: pageInfo.value?.canvasWidth,
      canvasHeight: pageInfo.value?.canvasHeight,
    });
    const res = await saveVersion({pageId: pageId.value, configJson, changeLog: changeLog.value});
    if (res.success && isPublishAfterSave.value) {
      publishing.value = true;
      await publishVersion({pageId: pageId.value, versionId: res.result.id});
    }
    saveModalVisible.value = false;
    changeLog.value = '';
    message.success(isPublishAfterSave.value ? '保存并发布成功' : '保存成功');
  } finally {
    saving.value = false;
    publishing.value = false;
  }
}

function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2, 9);
}
</script>


<style lang="less" scoped>
.panel-design-container {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #f0f2f5;
}

.design-toolbar {
  height: 50px;
  background: #fff;
  border-bottom: 1px solid #e8e8e8;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;

  .toolbar-left {
    display: flex;
    align-items: center;
    gap: 16px;

    .page-title {
      font-size: 16px;
      font-weight: 500;
    }
  }

  .toolbar-center {
    display: flex;
    align-items: center;
    gap: 8px;

    .zoom-control {
      display: flex;
      align-items: center;
      gap: 4px;

      .zoom-value {
        min-width: 50px;
        text-align: center;
      }
    }
  }

  .toolbar-right {
    display: flex;
    gap: 8px;
  }
}

.design-content {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.widget-panel {
  width: 220px;
  background: #fff;
  border-right: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;

  .panel-header {
    padding: 12px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    border-bottom: 1px solid #e8e8e8;
    font-weight: 500;
  }

  :deep(.ant-collapse) {
    flex: 1;
    overflow-y: auto;
  }

  .widget-grid {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
  }

  .widget-item {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 8px 4px;
    border: 1px solid #e8e8e8;
    border-radius: 4px;
    cursor: grab;
    transition: all 0.2s;

    &:hover {
      border-color: #1890ff;
      background: #e6f7ff;
    }

    .widget-icon {
      font-size: 20px;
      margin-bottom: 4px;
    }

    .widget-name {
      font-size: 12px;
      text-align: center;
    }
  }
}

.canvas-panel {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: auto;
  background: #e8e8e8;

  .canvas-wrapper {
    display: flex;
    align-items: center;
    justify-content: center;
    min-width: 100%;
    min-height: 100%;
  }

  .canvas {
    background: #fff;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
    position: relative;

    .canvas-grid {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background-image: linear-gradient(rgba(0, 0, 0, 0.05) 1px, transparent 1px),
      linear-gradient(90deg, rgba(0, 0, 0, 0.05) 1px, transparent 1px);
      background-size: 10px 10px;
      pointer-events: none;
    }
  }
}

.widget-wrapper {
  position: absolute;
  border: 1px solid transparent;
  cursor: move;

  &.selected {
    border-color: #1890ff;
    box-shadow: 0 0 0 1px #1890ff;
  }

  .resize-handle {
    position: absolute;
    width: 8px;
    height: 8px;
    background: #1890ff;
    border: 1px solid #fff;

    &.se {
      right: -4px;
      bottom: -4px;
      cursor: se-resize;
    }
  }
}

.property-panel {
  width: 320px;
  background: #fff;
  border-left: 1px solid #e8e8e8;
  display: flex;
  flex-direction: column;

  :deep(.ant-tabs) {
    height: 100%;
    display: flex;
    flex-direction: column;

    .ant-tabs-content {
      flex: 1;
      overflow-y: auto;
    }
  }

  .property-content {
    padding: 12px;
  }

  .no-selection {
    padding: 60px 16px;
    text-align: center;
    color: #999;

    p {
      margin-top: 16px;
    }
  }
}
</style>
