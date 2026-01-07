<template>
  <div class="panel-list-container">
    <BasicTable @register="registerTable" :rowSelection="rowSelection">
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined">新增面板</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '页面管理',
              onClick: handlePageManage.bind(null, record),
            },
            {
              label: '编辑',
              onClick: handleEdit.bind(null, record),
            },
            {
              label: '删除',
              color: 'error',
              popConfirm: {
                title: '确定删除该面板吗？',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
    
    <!-- 面板表单弹窗 -->
    <PanelModal @register="registerModal" @success="handleSuccess" />
    
    <!-- 页面管理抽屉 -->
    <PageDrawer @register="registerDrawer" />
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { BasicTable, useTable, TableAction } from '/@/components/Table';
import { useModal } from '/@/components/Modal';
import { useDrawer } from '/@/components/Drawer';
import { getPanelList, deletePanel } from './api/panel.api';
import { panelColumns, panelSearchFormSchema } from './data/panel.data';
import PanelModal from './components/PanelModal.vue';
import PageDrawer from './components/PageDrawer.vue';

const [registerModal, { openModal }] = useModal();
const [registerDrawer, { openDrawer }] = useDrawer();

const [registerTable, { reload }] = useTable({
  title: '控制面板列表',
  api: getPanelList,
  columns: panelColumns,
  formConfig: {
    labelWidth: 80,
    schemas: panelSearchFormSchema,
    autoSubmitOnEnter: true,
    showAdvancedButton: false,
    baseColProps: { span: 6 },
    actionColOptions: { span: 6 },
  },
  useSearchForm: true,
  showTableSetting: true,
  bordered: true,
  actionColumn: {
    width: 200,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});

const rowSelection = ref({
  type: 'checkbox',
});

function handleAdd() {
  openModal(true, { isUpdate: false });
}

function handleEdit(record) {
  openModal(true, { isUpdate: true, record });
}

function handleDelete(record) {
  deletePanel({ id: record.id }, () => {
    reload();
  });
}

function handlePageManage(record) {
  openDrawer(true, { panelId: record.id, panelName: record.panelName });
}

function handleSuccess() {
  reload();
}
</script>

<style lang="less" scoped>
.panel-list-container {
  padding: 16px;
}
</style>
