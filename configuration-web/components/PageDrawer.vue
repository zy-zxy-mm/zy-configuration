<template>
  <BasicDrawer
    v-bind="$attrs"
    @register="registerDrawer"
    :title="`页面管理 - ${panelName}`"
    width="900px"
    :showFooter="false"
  >
    <BasicTable @register="registerTable">
      <template #tableTitle>
        <a-button type="primary" @click="handleAdd" preIcon="ant-design:plus-outlined">新增页面</a-button>
      </template>
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '设计',
              onClick: handleDesign.bind(null, record),
            },
            {
              label: '预览',
              onClick: handlePreview.bind(null, record),
              ifShow: record.status === 1,
            },
            {
              label: '运行',
              onClick: handleRun.bind(null, record),
              ifShow: record.status === 1,
            },
            {
              label: '版本',
              onClick: handleVersion.bind(null, record),
            },
            {
              label: '编辑',
              onClick: handleEdit.bind(null, record),
            },
            {
              label: '删除',
              color: 'error',
              popConfirm: {
                title: '确定删除该页面吗？',
                confirm: handleDelete.bind(null, record),
              },
            },
          ]"
        />
      </template>
    </BasicTable>
    
    <!-- 页面表单弹窗 -->
    <PageModal @register="registerModal" @success="handleSuccess" :panelId="panelId" />
    
    <!-- 版本管理弹窗 -->
    <VersionModal @register="registerVersionModal" />
  </BasicDrawer>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { BasicDrawer, useDrawerInner } from '/@/components/Drawer';
import { BasicTable, useTable, TableAction } from '/@/components/Table';
import { useModal } from '/@/components/Modal';
import { getPageList, deletePage } from '../api/panel.api';
import { pageColumns } from '../data/panel.data';
import PageModal from './PageModal.vue';
import VersionModal from './VersionModal.vue';

const router = useRouter();
const panelId = ref('');
const panelName = ref('');

const [registerModal, { openModal }] = useModal();
const [registerVersionModal, { openModal: openVersionModal }] = useModal();

const [registerTable, { reload, setTableData }] = useTable({
  api: getPageList,
  columns: pageColumns,
  immediate: false,
  showTableSetting: false,
  bordered: true,
  actionColumn: {
    width: 280,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});

const [registerDrawer, { setDrawerProps }] = useDrawerInner(async (data) => {
  panelId.value = data.panelId;
  panelName.value = data.panelName;
  reload({ searchInfo: { panelId: data.panelId } });
});

function handleAdd() {
  openModal(true, { isUpdate: false });
}

function handleEdit(record) {
  openModal(true, { isUpdate: true, record });
}

function handleDelete(record) {
  deletePage({ id: record.id }, () => {
    reload({ searchInfo: { panelId: panelId.value } });
  });
}

function handleDesign(record) {
  router.push({
    path: '/smartPark/configuration/PanelDesign',
    query: { pageId: record.id },
  });
  console.log(router.push);
}

function handlePreview(record) {
  router.push({
    path: '/smartPark/configuration/PanelPreview',
    query: { pageId: record.id },
  });
}

function handleRun(record) {
  router.push({
    path: '/smartPark/configuration/PanelRuntime',
    query: { pageId: record.id },
  });
}

function handleVersion(record) {
  openVersionModal(true, { pageId: record.id, pageName: record.pageName });
}

function handleSuccess() {
  reload({ searchInfo: { panelId: panelId.value } });
}
</script>
