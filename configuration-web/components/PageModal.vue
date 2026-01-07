<template>
  <BasicModal
    v-bind="$attrs"
    @register="registerModal"
    :title="getTitle"
    @ok="handleSubmit"
    width="500px"
  >
    <BasicForm @register="registerForm" />
  </BasicModal>
</template>

<script lang="ts" setup>
import { ref, computed, unref } from 'vue';
import { BasicModal, useModalInner } from '/@/components/Modal';
import { BasicForm, useForm } from '/@/components/Form';
import { pageFormSchema } from '../data/panel.data';
import { addPage, editPage } from '../api/panel.api';

const props = defineProps({
  panelId: {
    type: String,
    default: '',
  },
});

const emit = defineEmits(['success', 'register']);

const isUpdate = ref(false);
const recordId = ref('');

const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
  labelWidth: 100,
  schemas: pageFormSchema,
  showActionButtonGroup: false,
});

const [registerModal, { setModalProps, closeModal }] = useModalInner(async (data) => {
  resetFields();
  isUpdate.value = !!data?.isUpdate;
  
  if (unref(isUpdate) && data.record) {
    recordId.value = data.record.id;
    setFieldsValue({
      ...data.record,
    });
  }
});

const getTitle = computed(() => (!unref(isUpdate) ? '新增页面' : '编辑页面'));

async function handleSubmit() {
  try {
    const values = await validate();
    setModalProps({ confirmLoading: true });
    
    if (unref(isUpdate)) {
      await editPage({ ...values, id: recordId.value });
    } else {
      await addPage({ ...values, panelId: props.panelId });
    }
    
    closeModal();
    emit('success');
  } finally {
    setModalProps({ confirmLoading: false });
  }
}
</script>
