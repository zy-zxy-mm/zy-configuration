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
import { panelFormSchema } from '../data/panel.data';
import { addPanel, editPanel } from '../api/panel.api';

const emit = defineEmits(['success', 'register']);

const isUpdate = ref(false);
const recordId = ref('');

const [registerForm, { resetFields, setFieldsValue, validate }] = useForm({
  labelWidth: 100,
  schemas: panelFormSchema,
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

const getTitle = computed(() => (!unref(isUpdate) ? '新增面板' : '编辑面板'));

async function handleSubmit() {
  try {
    const values = await validate();
    setModalProps({ confirmLoading: true });
    
    if (unref(isUpdate)) {
      await editPanel({ ...values, id: recordId.value });
    } else {
      await addPanel(values);
    }
    
    closeModal();
    emit('success');
  } finally {
    setModalProps({ confirmLoading: false });
  }
}
</script>
