/**
 * 组件库配置 - 丰富的智能设备控件
 */

// 组件分类
export const widgetCategories = [
  { key: 'basic', name: '基础控件' },
  { key: 'lighting', name: '照明控制' },
  { key: 'hvac', name: '暖通空调' },
  { key: 'security', name: '安防设备' },
  { key: 'sensor', name: '传感器' },
  { key: 'display', name: '显示控件' },
  { key: 'input', name: '输入控件' },
];

// 组件定义
export const widgetDefinitions = [
  // ========== 基础控件 ==========
  {
    type: 'button',
    name: '按钮',
    category: 'basic',
    icon: 'PlayCircleOutlined',
    defaultSize: { width: 120, height: 40 },
    props: {
      title: { type: 'string', default: '按钮', label: '按钮文字' },
      buttonType: { type: 'select', default: 'primary', label: '按钮类型', options: ['primary', 'default', 'dashed', 'danger'] },
      size: { type: 'select', default: 'middle', label: '尺寸', options: ['large', 'middle', 'small'] },
    },
    events: [
      { key: 'click', name: '点击事件', supportHttp: true },
    ],
  },
  {
    type: 'text',
    name: '文本',
    category: 'basic',
    icon: 'FontSizeOutlined',
    defaultSize: { width: 150, height: 30 },
    props: {
      content: { type: 'string', default: '文本内容', label: '文本内容' },
      fontSize: { type: 'number', default: 14, label: '字体大小', min: 10, max: 72 },
      fontWeight: { type: 'select', default: 'normal', label: '字体粗细', options: ['normal', 'bold', 'lighter'] },
      color: { type: 'color', default: '#333333', label: '文字颜色' },
      textAlign: { type: 'select', default: 'left', label: '对齐方式', options: ['left', 'center', 'right'] },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定数据点位',
    },
  },
  {
    type: 'image',
    name: '图片',
    category: 'basic',
    icon: 'PictureOutlined',
    defaultSize: { width: 200, height: 150 },
    props: {
      src: { type: 'string', default: '', label: '图片地址' },
      fit: { type: 'select', default: 'contain', label: '填充方式', options: ['contain', 'cover', 'fill', 'none'] },
      borderRadius: { type: 'number', default: 0, label: '圆角', min: 0, max: 100 },
    },
  },
  {
    type: 'icon',
    name: '图标',
    category: 'basic',
    icon: 'StarOutlined',
    defaultSize: { width: 48, height: 48 },
    props: {
      iconName: { type: 'icon', default: 'BulbOutlined', label: '图标' },
      iconSize: { type: 'number', default: 32, label: '图标大小', min: 12, max: 128 },
      color: { type: 'color', default: '#1890ff', label: '图标颜色' },
    },
    binding: {
      supportStateBinding: true,
      stateColors: { on: '#52c41a', off: '#999999' },
    },
  },
  {
    type: 'container',
    name: '容器',
    category: 'basic',
    icon: 'BorderOutlined',
    defaultSize: { width: 300, height: 200 },
    props: {
      backgroundColor: { type: 'color', default: '#ffffff', label: '背景色' },
      borderWidth: { type: 'number', default: 1, label: '边框宽度', min: 0, max: 10 },
      borderColor: { type: 'color', default: '#e8e8e8', label: '边框颜色' },
      borderRadius: { type: 'number', default: 8, label: '圆角', min: 0, max: 50 },
      shadow: { type: 'boolean', default: true, label: '显示阴影' },
    },
  },

  // ========== 照明控制 ==========
  {
    type: 'switch',
    name: '开关',
    category: 'lighting',
    icon: 'BulbOutlined',
    defaultSize: { width: 120, height: 50 },
    props: {
      title: { type: 'string', default: '开关', label: '标题' },
      showLabel: { type: 'boolean', default: true, label: '显示标签' },
      checkedText: { type: 'string', default: '开', label: '开启文字' },
      uncheckedText: { type: 'string', default: '关', label: '关闭文字' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'boolean',
      bindingLabel: '绑定开关状态点位',
    },
    events: [
      { key: 'change', name: '状态变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'dimmer',
    name: '调光器',
    category: 'lighting',
    icon: 'ControlOutlined',
    defaultSize: { width: 200, height: 80 },
    props: {
      title: { type: 'string', default: '亮度', label: '标题' },
      min: { type: 'number', default: 0, label: '最小值' },
      max: { type: 'number', default: 100, label: '最大值' },
      step: { type: 'number', default: 1, label: '步长' },
      unit: { type: 'string', default: '%', label: '单位' },
      showValue: { type: 'boolean', default: true, label: '显示数值' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定亮度点位',
    },
    events: [
      { key: 'change', name: '亮度变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'colorPicker',
    name: '色温/颜色',
    category: 'lighting',
    icon: 'BgColorsOutlined',
    defaultSize: { width: 200, height: 100 },
    props: {
      title: { type: 'string', default: '色温', label: '标题' },
      mode: { type: 'select', default: 'temperature', label: '模式', options: ['temperature', 'rgb'] },
      minTemp: { type: 'number', default: 2700, label: '最低色温(K)' },
      maxTemp: { type: 'number', default: 6500, label: '最高色温(K)' },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定色温/颜色点位',
    },
    events: [
      { key: 'change', name: '颜色变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'lightScene',
    name: '灯光场景',
    category: 'lighting',
    icon: 'AppstoreOutlined',
    defaultSize: { width: 280, height: 120 },
    props: {
      title: { type: 'string', default: '场景模式', label: '标题' },
      scenes: {
        type: 'array',
        default: [
          { key: 'day', name: '日间模式', icon: 'BulbOutlined' },
          { key: 'night', name: '夜间模式', icon: 'BulbFilled' },
          { key: 'movie', name: '影院模式', icon: 'PlayCircleOutlined' },
          { key: 'reading', name: '阅读模式', icon: 'ReadOutlined' },
        ],
        label: '场景列表',
      },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定当前场景点位',
    },
    events: [
      { key: 'select', name: '选择场景', supportHttp: true, params: ['sceneKey'] },
    ],
  },

  // ========== 暖通空调 ==========
  {
    type: 'acCard',
    name: '空调卡片',
    category: 'hvac',
    icon: 'CloudOutlined',
    defaultSize: { width: 320, height: 280 },
    props: {
      title: { type: 'string', default: '空调', label: '标题' },
      minTemp: { type: 'number', default: 16, label: '最低温度' },
      maxTemp: { type: 'number', default: 30, label: '最高温度' },
      modes: {
        type: 'array',
        default: [
          { key: 'cool', name: '制冷', icon: 'CloudOutlined' },
          { key: 'heat', name: '制热', icon: 'FireOutlined' },
          { key: 'auto', name: '自动', icon: 'SyncOutlined' },
          { key: 'fan', name: '送风', icon: 'SwapOutlined' },
          { key: 'dry', name: '除湿', icon: 'ExperimentOutlined' },
        ],
        label: '模式列表',
      },
      fanSpeeds: {
        type: 'array',
        default: [
          { key: 'auto', name: '自动' },
          { key: 'low', name: '低风' },
          { key: 'medium', name: '中风' },
          { key: 'high', name: '高风' },
        ],
        label: '风速列表',
      },
    },
    binding: {
      points: [
        { key: 'power', label: '电源状态', dataType: 'boolean' },
        { key: 'tempSet', label: '设定温度', dataType: 'number' },
        { key: 'tempRoom', label: '室内温度', dataType: 'number' },
        { key: 'mode', label: '运行模式', dataType: 'string' },
        { key: 'fanSpeed', label: '风速', dataType: 'string' },
      ],
    },
    events: [
      { key: 'powerChange', name: '电源开关', supportHttp: true, params: ['power'] },
      { key: 'tempChange', name: '温度调节', supportHttp: true, params: ['temp'] },
      { key: 'modeChange', name: '模式切换', supportHttp: true, params: ['mode'] },
      { key: 'fanSpeedChange', name: '风速调节', supportHttp: true, params: ['fanSpeed'] },
    ],
  },
  {
    type: 'thermostat',
    name: '温控器',
    category: 'hvac',
    icon: 'DashboardOutlined',
    defaultSize: { width: 200, height: 200 },
    props: {
      title: { type: 'string', default: '温控器', label: '标题' },
      minTemp: { type: 'number', default: 5, label: '最低温度' },
      maxTemp: { type: 'number', default: 35, label: '最高温度' },
      step: { type: 'number', default: 0.5, label: '调节步长' },
    },
    binding: {
      points: [
        { key: 'setTemp', label: '设定温度', dataType: 'number' },
        { key: 'currentTemp', label: '当前温度', dataType: 'number' },
      ],
    },
    events: [
      { key: 'tempChange', name: '温度调节', supportHttp: true, params: ['temp'] },
    ],
  },
  {
    type: 'fanControl',
    name: '风扇控制',
    category: 'hvac',
    icon: 'LoadingOutlined',
    defaultSize: { width: 180, height: 120 },
    props: {
      title: { type: 'string', default: '风扇', label: '标题' },
      speeds: {
        type: 'array',
        default: [
          { key: 'off', name: '关闭', value: 0 },
          { key: 'low', name: '低速', value: 1 },
          { key: 'medium', name: '中速', value: 2 },
          { key: 'high', name: '高速', value: 3 },
        ],
        label: '档位列表',
      },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定风扇档位点位',
    },
    events: [
      { key: 'speedChange', name: '档位变化', supportHttp: true, params: ['speed'] },
    ],
  },


  // ========== 安防设备 ==========
  {
    type: 'doorLock',
    name: '门锁控制',
    category: 'security',
    icon: 'LockOutlined',
    defaultSize: { width: 160, height: 80 },
    props: {
      title: { type: 'string', default: '门锁', label: '标题' },
      showStatus: { type: 'boolean', default: true, label: '显示状态' },
    },
    binding: {
      points: [
        { key: 'locked', label: '锁定状态', dataType: 'boolean' },
        { key: 'battery', label: '电池电量', dataType: 'number' },
      ],
    },
    events: [
      { key: 'lock', name: '锁门', supportHttp: true },
      { key: 'unlock', name: '开锁', supportHttp: true },
    ],
  },
  {
    type: 'gateControl',
    name: '闸机控制',
    category: 'security',
    icon: 'LoginOutlined',
    defaultSize: { width: 200, height: 100 },
    props: {
      title: { type: 'string', default: '闸机', label: '标题' },
      gateType: { type: 'select', default: 'barrier', label: '闸机类型', options: ['barrier', 'turnstile', 'door'] },
    },
    binding: {
      points: [
        { key: 'status', label: '闸机状态', dataType: 'string' },
        { key: 'isOpen', label: '开启状态', dataType: 'boolean' },
      ],
    },
    events: [
      { key: 'open', name: '开闸', supportHttp: true },
      { key: 'close', name: '关闸', supportHttp: true },
    ],
  },
  {
    type: 'camera',
    name: '摄像头',
    category: 'security',
    icon: 'VideoCameraOutlined',
    defaultSize: { width: 320, height: 240 },
    props: {
      title: { type: 'string', default: '摄像头', label: '标题' },
      streamUrl: { type: 'string', default: '', label: '视频流地址' },
      showControls: { type: 'boolean', default: true, label: '显示控制按钮' },
    },
    binding: {
      points: [
        { key: 'online', label: '在线状态', dataType: 'boolean' },
      ],
    },
    events: [
      { key: 'snapshot', name: '截图', supportHttp: true },
      { key: 'ptzControl', name: 'PTZ控制', supportHttp: true, params: ['direction', 'speed'] },
    ],
  },
  {
    type: 'alarm',
    name: '报警器',
    category: 'security',
    icon: 'AlertOutlined',
    defaultSize: { width: 120, height: 80 },
    props: {
      title: { type: 'string', default: '报警', label: '标题' },
      alarmType: { type: 'select', default: 'intrusion', label: '报警类型', options: ['intrusion', 'fire', 'smoke', 'gas', 'water'] },
    },
    binding: {
      points: [
        { key: 'armed', label: '布防状态', dataType: 'boolean' },
        { key: 'triggered', label: '触发状态', dataType: 'boolean' },
      ],
    },
    events: [
      { key: 'arm', name: '布防', supportHttp: true },
      { key: 'disarm', name: '撤防', supportHttp: true },
      { key: 'silence', name: '消音', supportHttp: true },
    ],
  },

  // ========== 传感器 ==========
  {
    type: 'tempHumidity',
    name: '温湿度',
    category: 'sensor',
    icon: 'CloudOutlined',
    defaultSize: { width: 180, height: 100 },
    props: {
      title: { type: 'string', default: '温湿度', label: '标题' },
      showIcon: { type: 'boolean', default: true, label: '显示图标' },
      tempUnit: { type: 'select', default: 'C', label: '温度单位', options: ['C', 'F'] },
    },
    binding: {
      points: [
        { key: 'temperature', label: '温度', dataType: 'number' },
        { key: 'humidity', label: '湿度', dataType: 'number' },
      ],
    },
  },
  {
    type: 'airQuality',
    name: '空气质量',
    category: 'sensor',
    icon: 'CloudOutlined',
    defaultSize: { width: 200, height: 120 },
    props: {
      title: { type: 'string', default: '空气质量', label: '标题' },
      showDetails: { type: 'boolean', default: true, label: '显示详情' },
    },
    binding: {
      points: [
        { key: 'pm25', label: 'PM2.5', dataType: 'number' },
        { key: 'pm10', label: 'PM10', dataType: 'number' },
        { key: 'co2', label: 'CO2', dataType: 'number' },
        { key: 'tvoc', label: 'TVOC', dataType: 'number' },
        { key: 'aqi', label: 'AQI指数', dataType: 'number' },
      ],
    },
  },
  {
    type: 'powerMeter',
    name: '电表',
    category: 'sensor',
    icon: 'ThunderboltOutlined',
    defaultSize: { width: 200, height: 120 },
    props: {
      title: { type: 'string', default: '电表', label: '标题' },
      showChart: { type: 'boolean', default: false, label: '显示趋势图' },
    },
    binding: {
      points: [
        { key: 'power', label: '实时功率(W)', dataType: 'number' },
        { key: 'voltage', label: '电压(V)', dataType: 'number' },
        { key: 'current', label: '电流(A)', dataType: 'number' },
        { key: 'energy', label: '累计电量(kWh)', dataType: 'number' },
      ],
    },
  },
  {
    type: 'waterMeter',
    name: '水表',
    category: 'sensor',
    icon: 'ExperimentOutlined',
    defaultSize: { width: 180, height: 100 },
    props: {
      title: { type: 'string', default: '水表', label: '标题' },
    },
    binding: {
      points: [
        { key: 'flow', label: '瞬时流量(L/min)', dataType: 'number' },
        { key: 'total', label: '累计用量(m³)', dataType: 'number' },
      ],
    },
  },
  {
    type: 'occupancy',
    name: '人体感应',
    category: 'sensor',
    icon: 'UserOutlined',
    defaultSize: { width: 120, height: 80 },
    props: {
      title: { type: 'string', default: '人体感应', label: '标题' },
    },
    binding: {
      points: [
        { key: 'occupied', label: '有人状态', dataType: 'boolean' },
        { key: 'lastDetected', label: '最后检测时间', dataType: 'string' },
      ],
    },
  },
  {
    type: 'lightSensor',
    name: '光照传感器',
    category: 'sensor',
    icon: 'BulbOutlined',
    defaultSize: { width: 150, height: 80 },
    props: {
      title: { type: 'string', default: '光照度', label: '标题' },
      unit: { type: 'string', default: 'lux', label: '单位' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定光照度点位',
    },
  },

  // ========== 显示控件 ==========
  {
    type: 'gauge',
    name: '仪表盘',
    category: 'display',
    icon: 'DashboardOutlined',
    defaultSize: { width: 180, height: 180 },
    props: {
      title: { type: 'string', default: '仪表盘', label: '标题' },
      min: { type: 'number', default: 0, label: '最小值' },
      max: { type: 'number', default: 100, label: '最大值' },
      unit: { type: 'string', default: '', label: '单位' },
      thresholds: {
        type: 'array',
        default: [
          { value: 30, color: '#52c41a' },
          { value: 70, color: '#faad14' },
          { value: 100, color: '#f5222d' },
        ],
        label: '阈值颜色',
      },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定数值点位',
    },
  },
  {
    type: 'progressBar',
    name: '进度条',
    category: 'display',
    icon: 'LineOutlined',
    defaultSize: { width: 200, height: 40 },
    props: {
      title: { type: 'string', default: '', label: '标题' },
      showPercent: { type: 'boolean', default: true, label: '显示百分比' },
      strokeColor: { type: 'color', default: '#1890ff', label: '进度条颜色' },
      trailColor: { type: 'color', default: '#f0f0f0', label: '背景颜色' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定进度值(0-100)',
    },
  },
  {
    type: 'statusIndicator',
    name: '状态指示灯',
    category: 'display',
    icon: 'CheckCircleOutlined',
    defaultSize: { width: 100, height: 60 },
    props: {
      title: { type: 'string', default: '状态', label: '标题' },
      onColor: { type: 'color', default: '#52c41a', label: '在线颜色' },
      offColor: { type: 'color', default: '#f5222d', label: '离线颜色' },
      onText: { type: 'string', default: '在线', label: '在线文字' },
      offText: { type: 'string', default: '离线', label: '离线文字' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'boolean',
      bindingLabel: '绑定状态点位',
    },
  },
  {
    type: 'valueDisplay',
    name: '数值显示',
    category: 'display',
    icon: 'FieldNumberOutlined',
    defaultSize: { width: 150, height: 80 },
    props: {
      title: { type: 'string', default: '数值', label: '标题' },
      unit: { type: 'string', default: '', label: '单位' },
      precision: { type: 'number', default: 1, label: '小数位数', min: 0, max: 4 },
      fontSize: { type: 'number', default: 24, label: '数值字号', min: 12, max: 72 },
      color: { type: 'color', default: '#1890ff', label: '数值颜色' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定数值点位',
    },
  },

  // ========== 输入控件 ==========
  {
    type: 'slider',
    name: '滑块',
    category: 'input',
    icon: 'ControlOutlined',
    defaultSize: { width: 200, height: 50 },
    props: {
      title: { type: 'string', default: '滑块', label: '标题' },
      min: { type: 'number', default: 0, label: '最小值' },
      max: { type: 'number', default: 100, label: '最大值' },
      step: { type: 'number', default: 1, label: '步长' },
      showValue: { type: 'boolean', default: true, label: '显示数值' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定数值点位',
    },
    events: [
      { key: 'change', name: '值变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'select',
    name: '下拉选择',
    category: 'input',
    icon: 'DownOutlined',
    defaultSize: { width: 180, height: 40 },
    props: {
      title: { type: 'string', default: '', label: '标题' },
      placeholder: { type: 'string', default: '请选择', label: '占位文字' },
      options: {
        type: 'array',
        default: [
          { label: '选项1', value: '1' },
          { label: '选项2', value: '2' },
          { label: '选项3', value: '3' },
        ],
        label: '选项列表',
      },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定选中值点位',
    },
    events: [
      { key: 'change', name: '选择变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'inputNumber',
    name: '数字输入',
    category: 'input',
    icon: 'FieldNumberOutlined',
    defaultSize: { width: 150, height: 40 },
    props: {
      title: { type: 'string', default: '', label: '标题' },
      min: { type: 'number', default: 0, label: '最小值' },
      max: { type: 'number', default: 100, label: '最大值' },
      step: { type: 'number', default: 1, label: '步长' },
      precision: { type: 'number', default: 0, label: '小数位数' },
    },
    binding: {
      supportDataBinding: true,
      dataType: 'number',
      bindingLabel: '绑定数值点位',
    },
    events: [
      { key: 'change', name: '值变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'radioGroup',
    name: '单选组',
    category: 'input',
    icon: 'CheckCircleOutlined',
    defaultSize: { width: 250, height: 40 },
    props: {
      title: { type: 'string', default: '', label: '标题' },
      buttonStyle: { type: 'select', default: 'solid', label: '按钮样式', options: ['outline', 'solid'] },
      options: {
        type: 'array',
        default: [
          { label: '选项A', value: 'a' },
          { label: '选项B', value: 'b' },
          { label: '选项C', value: 'c' },
        ],
        label: '选项列表',
      },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定选中值点位',
    },
    events: [
      { key: 'change', name: '选择变化', supportHttp: true, params: ['value'] },
    ],
  },
  {
    type: 'segmented',
    name: '分段控制器',
    category: 'input',
    icon: 'AppstoreOutlined',
    defaultSize: { width: 280, height: 40 },
    props: {
      options: {
        type: 'array',
        default: [
          { label: '模式1', value: '1' },
          { label: '模式2', value: '2' },
          { label: '模式3', value: '3' },
        ],
        label: '选项列表',
      },
    },
    binding: {
      supportDataBinding: true,
      bindingLabel: '绑定选中值点位',
    },
    events: [
      { key: 'change', name: '选择变化', supportHttp: true, params: ['value'] },
    ],
  },
];

// HTTP事件配置模板
export interface HttpEventConfig {
  enabled: boolean;
  method: 'GET' | 'POST' | 'PUT' | 'DELETE';
  url: string;
  headers: Record<string, string>;
  bodyTemplate: string; // 支持变量替换，如 ${value}, ${deviceId}
  successMessage: string;
  errorMessage: string;
}

// 默认HTTP事件配置
export const defaultHttpEventConfig: HttpEventConfig = {
  enabled: false,
  method: 'POST',
  url: '',
  headers: {
    'Content-Type': 'application/json',
  },
  bodyTemplate: '{"value": "${value}"}',
  successMessage: '操作成功',
  errorMessage: '操作失败',
};

// 获取组件定义
export function getWidgetDefinition(type: string) {
  return widgetDefinitions.find(w => w.type === type);
}

// 按分类获取组件
export function getWidgetsByCategory(category: string) {
  return widgetDefinitions.filter(w => w.category === category);
}

// 创建组件实例
export function createWidgetInstance(type: string, x: number, y: number) {
  const definition = getWidgetDefinition(type);
  if (!definition) return null;

  const instance: any = {
    id: generateId(),
    type,
    x,
    y,
    width: definition.defaultSize.width,
    height: definition.defaultSize.height,
    props: {},
    binding: {
      // 运行时绑定的设备ID（设计时为空，运行时由面板实例指定）
      deviceId: '',
      // 点位绑定配置
      points: {},
    },
    events: {},
    style: {
      zIndex: 1,
    },
  };

  // 初始化属性默认值
  if (definition.props) {
    Object.entries(definition.props).forEach(([key, config]: [string, any]) => {
      instance.props[key] = config.default;
    });
  }

  // 初始化事件配置
  if (definition.events) {
    definition.events.forEach((event: any) => {
      instance.events[event.key] = {
        ...defaultHttpEventConfig,
      };
    });
  }

  return instance;
}

// 生成唯一ID
function generateId() {
  return Date.now().toString(36) + Math.random().toString(36).substr(2, 9);
}
