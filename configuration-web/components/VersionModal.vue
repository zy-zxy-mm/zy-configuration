<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="`版本管理 - ${pageName}`"
    :showOkBtn="false"
    width="800px"
  >
    <BasicTable @register="registerTable">
      <template #action="{ record }">
        <TableAction
          :actions="[
            {
              label: '预览',
              onClick: handlePreview.bind(null, record),
            },
            {
              label: '发布',
              onClick: handlePublish.bind(null, record),
              ifShow: record.isPublished !== 1,
            },
            {
              label: '已发布',
              disabled: true,
              ifShow: record.isPublished === 1,
            },
          ]"
        />
      </template>
    </BasicTable>
  </BasicModal>
</template>

<script lang="ts" setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { BasicTable, useTable, TableAction } from '/@/components/Table';
import { getVersionList, publishVersion } from '../api/panel.api';
import { versionColumns } from '../data/panel.data';

const router = useRouter();
const pageId = ref('');
const pageName = ref('');

const [registerTable, { reload }] = useTable({
  api: getVersionList,
  columns: versionColumns,
  immediate: false,
  showTableSetting: false,
  bordered: true,
  pagination: {
    pageSize: 10,
  },
  beforeFetch: (params) => {
    // 确保每次请求都带上 pageId
    return { ...params, pageId: pageId.value };
  },
  actionColumn: {
    width: 150,
    title: '操作',
    dataIndex: 'action',
    slots: { customRender: 'action' },
  },
});

const [registerModal] = useModalInner(async (data) => {
  pageId.value = data.pageId;
  pageName.value = data.pageName;
  reload({ searchInfo: { pageId: data.pageId } });
});

function handlePreview(record) {
  router.push({
    path: '/smartPark/configuration/PanelPreview',
    query: { pageId: pageId.value, versionId: record.id },
  });
}

async function handlePublish(record) {
  await publishVersion({ pageId: pageId.value, versionId: record.id });
  reload({ searchInfo: { pageId: pageId.value } });
}
</script>
