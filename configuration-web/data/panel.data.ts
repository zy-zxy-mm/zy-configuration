import { BasicColumn, FormSchema } from '/@/components/Table';

// 面板列表列定义
export const panelColumns: BasicColumn[] = [
  {
    title: '面板名称',
    align: 'center',
    dataIndex: 'panelName',
    width: 200,
  },
  {
    title: '备注',
    align: 'center',
    dataIndex: 'remark',
    width: 300,
  },
  {
    title: '创建人',
    align: 'center',
    dataIndex: 'createBy',
    width: 120,
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 面板表单配置
export const panelFormSchema: FormSchema[] = [
  {
    label: '面板名称',
    field: 'panelName',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入面板名称',
      maxlength: 100,
    },
  },
  {
    label: '备注',
    field: 'remark',
    component: 'InputTextArea',
    componentProps: {
      placeholder: '请输入备注',
      rows: 3,
    },
  },
];

// 面板搜索表单配置
export const panelSearchFormSchema: FormSchema[] = [
  {
    label: '面板名称',
    field: 'panelName',
    component: 'JInput',
    colProps: { span: 6 },
    componentProps: {
      placeholder: '请输入面板名称',
    },
  },
];


// 页面列表列定义
export const pageColumns: BasicColumn[] = [
  {
    title: '页面名称',
    align: 'center',
    dataIndex: 'pageName',
    width: 200,
  },
  {
    title: '画布尺寸',
    align: 'center',
    dataIndex: 'canvasSize',
    width: 150,
    customRender: ({ record }) => {
      return `${record.canvasWidth} x ${record.canvasHeight}`;
    },
  },
  {
    title: '状态',
    align: 'center',
    dataIndex: 'status',
    width: 100,
    customRender: ({ text }) => {
      return text === 1 ? '已发布' : '草稿';
    },
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];

// 页面表单配置
export const pageFormSchema: FormSchema[] = [
  {
    label: '页面名称',
    field: 'pageName',
    component: 'Input',
    required: true,
    componentProps: {
      placeholder: '请输入页面名称',
    },
  },
  {
    label: '画布宽度',
    field: 'canvasWidth',
    component: 'InputNumber',
    defaultValue: 390,
    componentProps: {
      placeholder: '请输入画布宽度',
      min: 100,
      max: 3840,
    },
  },
  {
    label: '画布高度',
    field: 'canvasHeight',
    component: 'InputNumber',
    defaultValue: 844,
    componentProps: {
      placeholder: '请输入画布高度',
      min: 100,
      max: 2160,
    },
  },
];

// 版本列表列定义
export const versionColumns: BasicColumn[] = [
  {
    title: '版本号',
    align: 'center',
    dataIndex: 'versionNo',
    width: 100,
    customRender: ({ text }) => `V${text}`,
  },
  {
    title: '变更说明',
    align: 'center',
    dataIndex: 'changeLog',
    width: 300,
  },
  {
    title: '发布状态',
    align: 'center',
    dataIndex: 'isPublished',
    width: 100,
    customRender: ({ text }) => {
      return text === 1 ? '已发布' : '未发布';
    },
  },
  {
    title: '创建人',
    align: 'center',
    dataIndex: 'createBy',
    width: 120,
  },
  {
    title: '创建时间',
    align: 'center',
    dataIndex: 'createTime',
    width: 180,
  },
];
