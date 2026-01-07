<template>
  <div class="widget-renderer" :class="[`widget-${widget.type}`]">
    <!-- 开关控件 -->
    <template v-if="widget.type === 'switch'">
      <div class="switch-widget">
        <span v-if="widget.props?.showLabel" class="switch-label">{{ widget.props?.title || '开关' }}</span>
        <a-switch 
          v-model:checked="localValue"
          :disabled="isDesign"
          :checkedChildren="widget.props?.checkedText"
          :unCheckedChildren="widget.props?.uncheckedText"
          @change="handleChange"
        />
      </div>
    </template>
    
    <!-- 滑块控件 -->
    <template v-else-if="widget.type === 'slider' || widget.type === 'dimmer'">
      <div class="slider-widget">
        <span class="slider-label">{{ widget.props?.title || '滑块' }}</span>
        <a-slider 
          v-model:value="localValue" 
          :disabled="isDesign"
          :min="widget.props?.min || 0"
          :max="widget.props?.max || 100"
          :step="widget.props?.step || 1"
          @change="handleChange"
          style="flex: 1"
        />
        <span v-if="widget.props?.showValue !== false" class="slider-value">
          {{ localValue }}{{ widget.props?.unit || '' }}
        </span>
      </div>
    </template>
    
    <!-- 空调卡片控件 -->
    <template v-else-if="widget.type === 'acCard'">
      <div class="ac-card-widget">
        <div class="ac-header">
          <span class="ac-title">{{ widget.props?.title || '空调' }}</span>
          <a-switch 
            v-model:checked="acState.power" 
            :disabled="isDesign"
            @change="handleAcPowerChange"
          />
        </div>
        <div class="ac-body">
          <div class="ac-temp">
            <span class="temp-value">{{ acState.tempSet }}°C</span>
            <span class="temp-label">设定温度</span>
          </div>
          <div class="ac-controls">
            <a-button size="small" :disabled="isDesign" @click="handleTempChange(-1)">-</a-button>
            <a-button size="small" :disabled="isDesign" @click="handleTempChange(1)">+</a-button>
          </div>
        </div>
        <div class="ac-mode">
          <a-radio-group 
            v-model:value="acState.mode" 
            :disabled="isDesign"
            size="small"
            button-style="solid"
            @change="handleModeChange"
          >
            <a-radio-button 
              v-for="opt in modeOptions" 
              :key="opt.value" 
              :value="opt.value"
            >
              {{ opt.label }}
            </a-radio-button>
          </a-radio-group>
        </div>
        <div class="ac-footer">
          <span>室温: {{ acState.roomTemp }}°C</span>
          <a-select 
            v-model:value="acState.fanSpeed" 
            size="small" 
            style="width: 80px"
            :disabled="isDesign"
            @change="handleFanSpeedChange"
          >
            <a-select-option v-for="fan in fanSpeedOptions" :key="fan.key" :value="fan.key">
              {{ fan.name }}
            </a-select-option>
          </a-select>
        </div>
      </div>
    </template>
    
    <!-- 按钮控件 -->
    <template v-else-if="widget.type === 'button'">
      <a-button 
        :type="widget.props?.buttonType || 'primary'"
        :size="widget.props?.size || 'middle'"
        :disabled="isDesign"
        @click="handleButtonClick"
        style="width: 100%; height: 100%"
      >
        {{ widget.props?.title || '按钮' }}
      </a-button>
    </template>
    
    <!-- 文本控件 -->
    <template v-else-if="widget.type === 'text'">
      <div 
        class="text-widget"
        :style="{
          fontSize: `${widget.props?.fontSize || 14}px`,
          fontWeight: widget.props?.fontWeight || 'normal',
          color: widget.props?.color || '#333',
          textAlign: widget.props?.textAlign || 'left',
        }"
      >
        {{ displayText }}
      </div>
    </template>
    
    <!-- 下拉选择控件 -->
    <template v-else-if="widget.type === 'select'">
      <div class="select-widget">
        <span v-if="widget.props?.title" class="select-label">{{ widget.props.title }}</span>
        <a-select 
          v-model:value="localValue"
          :placeholder="widget.props?.placeholder || '请选择'"
          :disabled="isDesign"
          style="flex: 1"
          @change="handleChange"
        >
          <a-select-option 
            v-for="opt in widget.props?.options || []" 
            :key="opt.value" 
            :value="opt.value"
          >
            {{ opt.label }}
          </a-select-option>
        </a-select>
      </div>
    </template>
    
    <!-- 单选组控件 -->
    <template v-else-if="widget.type === 'radioGroup'">
      <div class="radio-group-widget">
        <span v-if="widget.props?.title" class="radio-label">{{ widget.props.title }}</span>
        <a-radio-group 
          v-model:value="localValue"
          :disabled="isDesign"
          :buttonStyle="widget.props?.buttonStyle || 'solid'"
          @change="handleChange"
        >
          <a-radio-button 
            v-for="opt in widget.props?.options || []" 
            :key="opt.value" 
            :value="opt.value"
          >
            {{ opt.label }}
          </a-radio-button>
        </a-radio-group>
      </div>
    </template>
    
    <!-- 分段控制器 -->
    <template v-else-if="widget.type === 'segmented'">
      <a-segmented 
        v-model:value="localValue"
        :options="widget.props?.options?.map(o => ({ label: o.label, value: o.value })) || []"
        :disabled="isDesign"
        @change="handleChange"
      />
    </template>
    
    <!-- 数字输入控件 -->
    <template v-else-if="widget.type === 'inputNumber'">
      <div class="input-number-widget">
        <span v-if="widget.props?.title" class="input-label">{{ widget.props.title }}</span>
        <a-input-number 
          v-model:value="localValue"
          :min="widget.props?.min"
          :max="widget.props?.max"
          :step="widget.props?.step || 1"
          :precision="widget.props?.precision || 0"
          :disabled="isDesign"
          style="flex: 1"
          @change="handleChange"
        />
      </div>
    </template>
    
    <!-- 数值显示控件 -->
    <template v-else-if="widget.type === 'valueDisplay'">
      <div class="value-display-widget">
        <div class="value-title">{{ widget.props?.title || '数值' }}</div>
        <div 
          class="value-number"
          :style="{ 
            fontSize: `${widget.props?.fontSize || 24}px`,
            color: widget.props?.color || '#1890ff'
          }"
        >
          {{ formatValue(localValue, widget.props?.precision || 1) }}
          <span class="value-unit">{{ widget.props?.unit || '' }}</span>
        </div>
      </div>
    </template>
    
    <!-- 状态指示灯 -->
    <template v-else-if="widget.type === 'statusIndicator'">
      <div class="status-indicator-widget">
        <div 
          class="status-dot"
          :style="{ backgroundColor: localValue ? (widget.props?.onColor || '#52c41a') : (widget.props?.offColor || '#f5222d') }"
        ></div>
        <span class="status-text">
          {{ localValue ? (widget.props?.onText || '在线') : (widget.props?.offText || '离线') }}
        </span>
      </div>
    </template>
    
    <!-- 进度条 -->
    <template v-else-if="widget.type === 'progressBar'">
      <div class="progress-bar-widget">
        <span v-if="widget.props?.title" class="progress-label">{{ widget.props.title }}</span>
        <a-progress 
          :percent="localValue || 0"
          :showInfo="widget.props?.showPercent !== false"
          :strokeColor="widget.props?.strokeColor || '#1890ff'"
          :trailColor="widget.props?.trailColor || '#f0f0f0'"
        />
      </div>
    </template>
    
    <!-- 温湿度传感器 -->
    <template v-else-if="widget.type === 'tempHumidity'">
      <div class="temp-humidity-widget">
        <div class="th-title">{{ widget.props?.title || '温湿度' }}</div>
        <div class="th-values">
          <div class="th-item">
            <span class="th-value">{{ sensorState.temperature || '--' }}°{{ widget.props?.tempUnit || 'C' }}</span>
            <span class="th-label">温度</span>
          </div>
          <div class="th-item">
            <span class="th-value">{{ sensorState.humidity || '--' }}%</span>
            <span class="th-label">湿度</span>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 门锁控制 -->
    <template v-else-if="widget.type === 'doorLock'">
      <div class="door-lock-widget">
        <div class="lock-icon" :class="{ locked: lockState.locked }">
          <LockOutlined v-if="lockState.locked" />
          <UnlockOutlined v-else />
        </div>
        <div class="lock-info">
          <div class="lock-title">{{ widget.props?.title || '门锁' }}</div>
          <div class="lock-status">{{ lockState.locked ? '已锁定' : '已解锁' }}</div>
        </div>
        <div class="lock-actions">
          <a-button size="small" :disabled="isDesign" @click="handleLock">锁门</a-button>
          <a-button size="small" :disabled="isDesign" @click="handleUnlock">开锁</a-button>
        </div>
      </div>
    </template>
    
    <!-- 图片控件 -->
    <template v-else-if="widget.type === 'image'">
      <div class="image-widget">
        <img 
          v-if="widget.props?.src"
          :src="widget.props.src"
          :style="{
            objectFit: widget.props?.fit || 'contain',
            borderRadius: `${widget.props?.borderRadius || 0}px`,
          }"
        />
        <div v-else class="image-placeholder">
          <PictureOutlined />
          <span>图片</span>
        </div>
      </div>
    </template>
    
    <!-- 容器控件 -->
    <template v-else-if="widget.type === 'container'">
      <div 
        class="container-widget"
        :style="{
          backgroundColor: widget.props?.backgroundColor || '#fff',
          borderWidth: `${widget.props?.borderWidth || 1}px`,
          borderColor: widget.props?.borderColor || '#e8e8e8',
          borderRadius: `${widget.props?.borderRadius || 8}px`,
          boxShadow: widget.props?.shadow ? '0 2px 8px rgba(0,0,0,0.1)' : 'none',
        }"
      >
      </div>
    </template>
    
    <!-- 仪表盘控件 -->
    <template v-else-if="widget.type === 'gauge'">
      <div class="gauge-widget">
        <div class="gauge-title">{{ widget.props?.title || '仪表盘' }}</div>
        <div class="gauge-circle">
          <svg viewBox="0 0 100 100">
            <circle cx="50" cy="50" r="45" fill="none" stroke="#f0f0f0" stroke-width="8" />
            <circle 
              cx="50" cy="50" r="45" fill="none" 
              :stroke="getGaugeColor(localValue)" 
              stroke-width="8"
              stroke-linecap="round"
              :stroke-dasharray="`${(localValue / (widget.props?.max || 100)) * 283} 283`"
              transform="rotate(-90 50 50)"
            />
          </svg>
          <div class="gauge-value">
            {{ localValue || 0 }}
            <span class="gauge-unit">{{ widget.props?.unit || '' }}</span>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 温控器控件 -->
    <template v-else-if="widget.type === 'thermostat'">
      <div class="thermostat-widget">
        <div class="thermostat-title">{{ widget.props?.title || '温控器' }}</div>
        <div class="thermostat-display">
          <div class="current-temp">{{ thermostatState.currentTemp }}°</div>
          <div class="set-temp">
            <a-button size="small" :disabled="isDesign" @click="handleThermostatChange(-0.5)">-</a-button>
            <span>{{ thermostatState.setTemp }}°</span>
            <a-button size="small" :disabled="isDesign" @click="handleThermostatChange(0.5)">+</a-button>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 风扇控制控件 -->
    <template v-else-if="widget.type === 'fanControl'">
      <div class="fan-control-widget">
        <div class="fan-title">{{ widget.props?.title || '风扇' }}</div>
        <div class="fan-icon" :class="{ spinning: fanState.speed > 0 }">
          <LoadingOutlined :spin="fanState.speed > 0" />
        </div>
        <a-segmented 
          v-model:value="fanState.speed"
          :options="widget.props?.speeds?.map(s => ({ label: s.name, value: s.value })) || [
            { label: '关', value: 0 },
            { label: '低', value: 1 },
            { label: '中', value: 2 },
            { label: '高', value: 3 },
          ]"
          :disabled="isDesign"
          size="small"
          @change="handleFanChange"
        />
      </div>
    </template>
    
    <!-- 闸机控制控件 -->
    <template v-else-if="widget.type === 'gateControl'">
      <div class="gate-control-widget">
        <div class="gate-icon">
          <LoginOutlined />
        </div>
        <div class="gate-info">
          <div class="gate-title">{{ widget.props?.title || '闸机' }}</div>
          <div class="gate-status">{{ gateState.isOpen ? '已开启' : '已关闭' }}</div>
        </div>
        <div class="gate-actions">
          <a-button type="primary" size="small" :disabled="isDesign" @click="handleGateOpen">开闸</a-button>
          <a-button size="small" :disabled="isDesign" @click="handleGateClose">关闸</a-button>
        </div>
      </div>
    </template>
    
    <!-- 摄像头控件 -->
    <template v-else-if="widget.type === 'camera'">
      <div class="camera-widget">
        <div class="camera-header">
          <span>{{ widget.props?.title || '摄像头' }}</span>
          <span class="camera-status" :class="{ online: cameraState.online }">
            {{ cameraState.online ? '在线' : '离线' }}
          </span>
        </div>
        <div class="camera-preview">
          <VideoCameraOutlined />
          <span>视频预览</span>
        </div>
        <div v-if="widget.props?.showControls" class="camera-controls">
          <a-button size="small" :disabled="isDesign" @click="handleSnapshot">截图</a-button>
        </div>
      </div>
    </template>
    
    <!-- 报警器控件 -->
    <template v-else-if="widget.type === 'alarm'">
      <div class="alarm-widget" :class="{ triggered: alarmState.triggered }">
        <div class="alarm-icon">
          <AlertOutlined />
        </div>
        <div class="alarm-info">
          <div class="alarm-title">{{ widget.props?.title || '报警' }}</div>
          <div class="alarm-status">
            {{ alarmState.triggered ? '报警中' : (alarmState.armed ? '已布防' : '已撤防') }}
          </div>
        </div>
        <div class="alarm-actions">
          <a-button size="small" :disabled="isDesign" @click="handleArm">布防</a-button>
          <a-button size="small" :disabled="isDesign" @click="handleDisarm">撤防</a-button>
        </div>
      </div>
    </template>
    
    <!-- 空气质量控件 -->
    <template v-else-if="widget.type === 'airQuality'">
      <div class="air-quality-widget">
        <div class="aq-title">{{ widget.props?.title || '空气质量' }}</div>
        <div class="aq-main">
          <div class="aq-aqi" :style="{ color: getAqiColor(airQualityState.aqi) }">
            {{ airQualityState.aqi }}
            <span class="aq-level">{{ getAqiLevel(airQualityState.aqi) }}</span>
          </div>
        </div>
        <div v-if="widget.props?.showDetails" class="aq-details">
          <div class="aq-item"><span>PM2.5</span><span>{{ airQualityState.pm25 }}</span></div>
          <div class="aq-item"><span>CO2</span><span>{{ airQualityState.co2 }}</span></div>
        </div>
      </div>
    </template>
    
    <!-- 电表控件 -->
    <template v-else-if="widget.type === 'powerMeter'">
      <div class="power-meter-widget">
        <div class="pm-title">{{ widget.props?.title || '电表' }}</div>
        <div class="pm-main">
          <ThunderboltOutlined class="pm-icon" />
          <div class="pm-value">{{ powerMeterState.power }}<span>W</span></div>
        </div>
        <div class="pm-details">
          <div class="pm-item"><span>电压</span><span>{{ powerMeterState.voltage }}V</span></div>
          <div class="pm-item"><span>电流</span><span>{{ powerMeterState.current }}A</span></div>
          <div class="pm-item"><span>累计</span><span>{{ powerMeterState.energy }}kWh</span></div>
        </div>
      </div>
    </template>
    
    <!-- 水表控件 -->
    <template v-else-if="widget.type === 'waterMeter'">
      <div class="water-meter-widget">
        <div class="wm-title">{{ widget.props?.title || '水表' }}</div>
        <div class="wm-main">
          <ExperimentOutlined class="wm-icon" />
          <div class="wm-value">{{ waterMeterState.flow }}<span>L/min</span></div>
        </div>
        <div class="wm-total">累计: {{ waterMeterState.total }} m³</div>
      </div>
    </template>
    
    <!-- 人体感应控件 -->
    <template v-else-if="widget.type === 'occupancy'">
      <div class="occupancy-widget" :class="{ occupied: occupancyState.occupied }">
        <UserOutlined class="occupancy-icon" />
        <div class="occupancy-info">
          <div class="occupancy-title">{{ widget.props?.title || '人体感应' }}</div>
          <div class="occupancy-status">{{ occupancyState.occupied ? '有人' : '无人' }}</div>
        </div>
      </div>
    </template>
    
    <!-- 光照传感器控件 -->
    <template v-else-if="widget.type === 'lightSensor'">
      <div class="light-sensor-widget">
        <BulbOutlined class="ls-icon" />
        <div class="ls-info">
          <div class="ls-title">{{ widget.props?.title || '光照度' }}</div>
          <div class="ls-value">{{ localValue || 0 }} {{ widget.props?.unit || 'lux' }}</div>
        </div>
      </div>
    </template>
    
    <!-- 色温/颜色控件 -->
    <template v-else-if="widget.type === 'colorPicker'">
      <div class="color-picker-widget">
        <div class="cp-title">{{ widget.props?.title || '色温' }}</div>
        <div v-if="widget.props?.mode === 'temperature'" class="cp-temp">
          <a-slider 
            v-model:value="colorState.temperature"
            :min="widget.props?.minTemp || 2700"
            :max="widget.props?.maxTemp || 6500"
            :disabled="isDesign"
            @change="handleColorChange"
          />
          <span class="cp-value">{{ colorState.temperature }}K</span>
        </div>
        <div v-else class="cp-color">
          <input type="color" v-model="colorState.color" :disabled="isDesign" @change="handleColorChange" />
        </div>
      </div>
    </template>
    
    <!-- 灯光场景控件 -->
    <template v-else-if="widget.type === 'lightScene'">
      <div class="light-scene-widget">
        <div class="ls-title">{{ widget.props?.title || '场景模式' }}</div>
        <div class="ls-scenes">
          <div 
            v-for="scene in widget.props?.scenes || []" 
            :key="scene.key"
            class="ls-scene-item"
            :class="{ active: lightSceneState.current === scene.key }"
            @click="handleSceneSelect(scene.key)"
          >
            <component :is="getIconComponent(scene.icon)" />
            <span>{{ scene.name }}</span>
          </div>
        </div>
      </div>
    </template>
    
    <!-- 图标控件 -->
    <template v-else-if="widget.type === 'icon'">
      <div class="icon-widget" :style="{ color: widget.props?.color || '#1890ff' }">
        <component 
          :is="getIconComponent(widget.props?.iconName || 'BulbOutlined')" 
          :style="{ fontSize: `${widget.props?.iconSize || 32}px` }"
        />
      </div>
    </template>
    
    <!-- 默认占位 -->
    <template v-else>
      <div class="default-widget">
        <AppstoreOutlined />
        <span>{{ widget.type }}</span>
      </div>
    </template>
  </div>
</template>

<script lang="ts" setup>
import { ref, reactive, computed, watch, onMounted } from 'vue';
import * as Icons from '@ant-design/icons-vue';

const {
  LockOutlined, UnlockOutlined, PictureOutlined, AppstoreOutlined,
  LoadingOutlined, LoginOutlined, VideoCameraOutlined, AlertOutlined,
  ThunderboltOutlined, ExperimentOutlined, UserOutlined, BulbOutlined,
} = Icons;

function getIconComponent(iconName: string) {
  return (Icons as any)[iconName] || Icons.AppstoreOutlined;
}

const props = defineProps({
  widget: { type: Object, required: true },
  isDesign: { type: Boolean, default: false },
  shadowState: { type: Object, default: () => ({}) },
  deviceId: { type: String, default: '' },
});

const emit = defineEmits(['command', 'httpRequest']);

const localValue = ref<any>(false);

// 空调状态
const acState = reactive({
  power: false,
  tempSet: 26,
  roomTemp: 28,
  mode: 'cool',
  fanSpeed: 'auto',
});

// 温控器状态
const thermostatState = reactive({
  setTemp: 22,
  currentTemp: 24,
});

// 风扇状态
const fanState = reactive({
  speed: 0,
});

// 闸机状态
const gateState = reactive({
  isOpen: false,
  status: 'closed',
});

// 摄像头状态
const cameraState = reactive({
  online: true,
});

// 报警器状态
const alarmState = reactive({
  armed: false,
  triggered: false,
});

// 空气质量状态
const airQualityState = reactive({
  aqi: 65,
  pm25: 35,
  pm10: 50,
  co2: 450,
  tvoc: 0.3,
});

// 电表状态
const powerMeterState = reactive({
  power: 1250,
  voltage: 220,
  current: 5.7,
  energy: 1234.5,
});

// 水表状态
const waterMeterState = reactive({
  flow: 2.5,
  total: 156.8,
});

// 人体感应状态
const occupancyState = reactive({
  occupied: false,
});

// 色温状态
const colorState = reactive({
  temperature: 4000,
  color: '#ffffff',
});

// 灯光场景状态
const lightSceneState = reactive({
  current: '',
});

const modeOptions = computed(() => {
  return props.widget.props?.modes?.map((m: any) => ({ label: m.name, value: m.key })) || [
    { label: '制冷', value: 'cool' },
    { label: '制热', value: 'heat' },
    { label: '自动', value: 'auto' },
  ];
});

const fanSpeedOptions = computed(() => {
  return props.widget.props?.fanSpeeds || [
    { key: 'auto', name: '自动' },
    { key: 'low', name: '低风' },
    { key: 'medium', name: '中风' },
    { key: 'high', name: '高风' },
  ];
});

// 传感器状态
const sensorState = reactive({
  temperature: 25.5,
  humidity: 60,
});

// 门锁状态
const lockState = reactive({
  locked: true,
  battery: 80,
});

// 显示文本
const displayText = computed(() => {
  if (props.widget.binding?.point && props.shadowState) {
    const value = props.shadowState[props.widget.binding.point];
    if (value !== undefined) {
      return transformValue(value);
    }
  }
  return props.widget.props?.content || props.widget.props?.title || '文本';
});

// 值转换
function transformValue(value: any) {
  const binding = props.widget.binding;
  if (!binding?.enableTransform) return value;
  
  if (binding.transformType === 'mapping' && binding.mapping) {
    return binding.mapping[String(value)] ?? value;
  }
  if (binding.transformType === 'format' && binding.formatTemplate) {
    return binding.formatTemplate.replace('{value}', value);
  }
  return value;
}

// 格式化数值
function formatValue(value: any, precision: number) {
  if (value === null || value === undefined) return '--';
  const num = Number(value);
  if (isNaN(num)) return value;
  return num.toFixed(precision);
}

// 监听影子状态变化
watch(() => props.shadowState, (newState) => {
  if (!newState || props.isDesign) return;
  
  const binding = props.widget.binding;
  
  // 简单绑定
  if (binding?.point && newState[binding.point] !== undefined) {
    localValue.value = newState[binding.point];
  }
  
  // 多点位绑定（空调）
  if (props.widget.type === 'acCard' && binding?.points) {
    if (binding.points.power && newState[binding.points.power] !== undefined) {
      acState.power = !!newState[binding.points.power];
    }
    if (binding.points.tempSet && newState[binding.points.tempSet] !== undefined) {
      acState.tempSet = Number(newState[binding.points.tempSet]);
    }
    if (binding.points.tempRoom && newState[binding.points.tempRoom] !== undefined) {
      acState.roomTemp = Number(newState[binding.points.tempRoom]);
    }
    if (binding.points.mode && newState[binding.points.mode] !== undefined) {
      acState.mode = String(newState[binding.points.mode]);
    }
    if (binding.points.fanSpeed && newState[binding.points.fanSpeed] !== undefined) {
      acState.fanSpeed = String(newState[binding.points.fanSpeed]);
    }
  }
  
  // 温湿度
  if (props.widget.type === 'tempHumidity' && binding?.points) {
    if (binding.points.temperature && newState[binding.points.temperature] !== undefined) {
      sensorState.temperature = Number(newState[binding.points.temperature]);
    }
    if (binding.points.humidity && newState[binding.points.humidity] !== undefined) {
      sensorState.humidity = Number(newState[binding.points.humidity]);
    }
  }
  
  // 门锁
  if (props.widget.type === 'doorLock' && binding?.points) {
    if (binding.points.locked && newState[binding.points.locked] !== undefined) {
      lockState.locked = !!newState[binding.points.locked];
    }
  }
}, { deep: true, immediate: true });

// 事件处理
function handleChange(value: any) {
  if (props.isDesign) return;
  triggerEvent('change', { value });
}

function handleButtonClick() {
  if (props.isDesign) return;
  triggerEvent('click', {});
}

function handleAcPowerChange(value: boolean) {
  if (props.isDesign) return;
  triggerEvent('powerChange', { power: value });
}

function handleTempChange(delta: number) {
  if (props.isDesign) return;
  const minTemp = props.widget.props?.minTemp || 16;
  const maxTemp = props.widget.props?.maxTemp || 30;
  const newTemp = Math.max(minTemp, Math.min(maxTemp, acState.tempSet + delta));
  acState.tempSet = newTemp;
  triggerEvent('tempChange', { temp: newTemp });
}

function handleModeChange(value: string) {
  if (props.isDesign) return;
  triggerEvent('modeChange', { mode: value });
}

function handleFanSpeedChange(value: string) {
  if (props.isDesign) return;
  triggerEvent('fanSpeedChange', { fanSpeed: value });
}

function handleLock() {
  if (props.isDesign) return;
  triggerEvent('lock', {});
}

function handleUnlock() {
  if (props.isDesign) return;
  triggerEvent('unlock', {});
}

// 温控器事件
function handleThermostatChange(delta: number) {
  if (props.isDesign) return;
  const minTemp = props.widget.props?.minTemp || 5;
  const maxTemp = props.widget.props?.maxTemp || 35;
  const newTemp = Math.max(minTemp, Math.min(maxTemp, thermostatState.setTemp + delta));
  thermostatState.setTemp = newTemp;
  triggerEvent('tempChange', { temp: newTemp });
}

// 风扇事件
function handleFanChange(value: number) {
  if (props.isDesign) return;
  triggerEvent('speedChange', { speed: value });
}

// 闸机事件
function handleGateOpen() {
  if (props.isDesign) return;
  gateState.isOpen = true;
  triggerEvent('open', {});
}

function handleGateClose() {
  if (props.isDesign) return;
  gateState.isOpen = false;
  triggerEvent('close', {});
}

// 摄像头事件
function handleSnapshot() {
  if (props.isDesign) return;
  triggerEvent('snapshot', {});
}

// 报警器事件
function handleArm() {
  if (props.isDesign) return;
  alarmState.armed = true;
  triggerEvent('arm', {});
}

function handleDisarm() {
  if (props.isDesign) return;
  alarmState.armed = false;
  triggerEvent('disarm', {});
}

// 色温事件
function handleColorChange() {
  if (props.isDesign) return;
  const value = props.widget.props?.mode === 'temperature' ? colorState.temperature : colorState.color;
  triggerEvent('change', { value });
}

// 场景选择事件
function handleSceneSelect(sceneKey: string) {
  if (props.isDesign) return;
  lightSceneState.current = sceneKey;
  triggerEvent('select', { sceneKey });
}

// 仪表盘颜色
function getGaugeColor(value: number) {
  const thresholds = props.widget.props?.thresholds || [
    { value: 30, color: '#52c41a' },
    { value: 70, color: '#faad14' },
    { value: 100, color: '#f5222d' },
  ];
  for (const t of thresholds) {
    if (value <= t.value) return t.color;
  }
  return '#1890ff';
}

// AQI颜色
function getAqiColor(aqi: number) {
  if (aqi <= 50) return '#52c41a';
  if (aqi <= 100) return '#faad14';
  if (aqi <= 150) return '#fa8c16';
  if (aqi <= 200) return '#f5222d';
  return '#722ed1';
}

// AQI等级
function getAqiLevel(aqi: number) {
  if (aqi <= 50) return '优';
  if (aqi <= 100) return '良';
  if (aqi <= 150) return '轻度';
  if (aqi <= 200) return '中度';
  return '重度';
}

// 触发事件
function triggerEvent(eventKey: string, params: Record<string, any>) {
  const eventConfig = props.widget.events?.[eventKey];
  if (!eventConfig?.enabled || !eventConfig.url) {
    // 如果没有配置HTTP事件，发送默认命令
    emit('command', {
      deviceId: props.deviceId || props.widget.binding?.deviceId,
      commandKey: eventKey,
      params,
    });
    return;
  }
  
  // 发送HTTP请求
  emit('httpRequest', {
    eventKey,
    config: eventConfig,
    params: {
      ...params,
      deviceId: props.deviceId || props.widget.binding?.deviceId,
      timestamp: Date.now(),
    },
  });
}
</script>


<style lang="less" scoped>
.widget-renderer {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.switch-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  width: 100%;
  
  .switch-label {
    font-size: 14px;
    flex: 1;
  }
}

.slider-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  width: 100%;
  
  .slider-label {
    font-size: 14px;
    white-space: nowrap;
  }
  
  .slider-value {
    min-width: 40px;
    text-align: right;
    font-weight: 500;
  }
}

.ac-card-widget {
  width: 100%;
  height: 100%;
  padding: 12px 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  color: #fff;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  
  .ac-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 8px;
    flex-shrink: 0;
    
    .ac-title {
      font-size: 14px;
      font-weight: 600;
    }
  }
  
  .ac-body {
    flex: 1;
    display: flex;
    justify-content: space-between;
    align-items: center;
    min-height: 0;
    
    .ac-temp {
      .temp-value {
        font-size: 36px;
        font-weight: bold;
        line-height: 1;
      }
      
      .temp-label {
        display: block;
        font-size: 12px;
        opacity: 0.8;
        margin-top: 4px;
      }
    }
    
    .ac-controls {
      display: flex;
      flex-direction: column;
      gap: 6px;
    }
  }
  
  .ac-mode {
    margin: 8px 0;
    flex-shrink: 0;
    
    :deep(.ant-radio-group) {
      display: flex;
      width: 100%;
      
      .ant-radio-button-wrapper {
        flex: 1;
        text-align: center;
        background: rgba(255, 255, 255, 0.2);
        border-color: rgba(255, 255, 255, 0.3);
        color: #fff;
        font-size: 12px;
        padding: 0 8px;
        height: 28px;
        line-height: 26px;
        
        &:first-child {
          border-radius: 4px 0 0 4px;
        }
        
        &:last-child {
          border-radius: 0 4px 4px 0;
        }
        
        &:hover {
          color: #fff;
          background: rgba(255, 255, 255, 0.3);
        }
        
        &.ant-radio-button-wrapper-checked {
          background: rgba(255, 255, 255, 0.95);
          border-color: rgba(255, 255, 255, 0.95);
          color: #667eea;
          
          &:hover {
            color: #667eea;
            background: rgba(255, 255, 255, 0.95);
          }
        }
        
        &::before {
          background-color: rgba(255, 255, 255, 0.3) !important;
        }
      }
    }
  }
  
  .ac-footer {
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 12px;
    opacity: 0.9;
    flex-shrink: 0;
  }
}

.select-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  width: 100%;
  
  .select-label {
    font-size: 14px;
    white-space: nowrap;
  }
}

.radio-group-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  width: 100%;
  
  .radio-label {
    font-size: 14px;
    white-space: nowrap;
  }
}

.input-number-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  width: 100%;
  
  .input-label {
    font-size: 14px;
    white-space: nowrap;
  }
}

.value-display-widget {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  padding: 8px;
  
  .value-title {
    font-size: 12px;
    color: #666;
    margin-bottom: 4px;
  }
  
  .value-number {
    font-weight: bold;
    
    .value-unit {
      font-size: 0.5em;
      font-weight: normal;
      margin-left: 2px;
    }
  }
}

.status-indicator-widget {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 12px;
  
  .status-dot {
    width: 12px;
    height: 12px;
    border-radius: 50%;
    animation: pulse 2s infinite;
  }
  
  .status-text {
    font-size: 14px;
  }
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0.4);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(82, 196, 26, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(82, 196, 26, 0);
  }
}

.progress-bar-widget {
  width: 100%;
  padding: 0 12px;
  
  .progress-label {
    display: block;
    font-size: 12px;
    color: #666;
    margin-bottom: 4px;
  }
}

.temp-humidity-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  border-radius: 8px;
  color: #fff;
  
  .th-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .th-values {
    display: flex;
    justify-content: space-around;
    
    .th-item {
      text-align: center;
      
      .th-value {
        font-size: 24px;
        font-weight: bold;
        display: block;
      }
      
      .th-label {
        font-size: 12px;
        opacity: 0.8;
      }
    }
  }
}

.door-lock-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  
  .lock-icon {
    font-size: 28px;
    color: #f5222d;
    
    &.locked {
      color: #52c41a;
    }
  }
  
  .lock-info {
    flex: 1;
    
    .lock-title {
      font-size: 14px;
      font-weight: 500;
    }
    
    .lock-status {
      font-size: 12px;
      color: #666;
    }
  }
  
  .lock-actions {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
}

.image-widget {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  
  img {
    width: 100%;
    height: 100%;
  }
  
  .image-placeholder {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 8px;
    color: #999;
    font-size: 32px;
    
    span {
      font-size: 12px;
    }
  }
}

.container-widget {
  width: 100%;
  height: 100%;
  border-style: solid;
}

.text-widget {
  width: 100%;
  height: 100%;
  padding: 4px 8px;
  display: flex;
  align-items: center;
  word-break: break-word;
}

.default-widget {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  color: #999;
  font-size: 24px;
  
  span {
    font-size: 12px;
  }
}

// 仪表盘样式
.gauge-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  
  .gauge-title {
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .gauge-circle {
    position: relative;
    flex: 1;
    width: 100%;
    display: flex;
    align-items: center;
    justify-content: center;
    
    svg {
      width: 100%;
      height: 100%;
      max-width: 150px;
      max-height: 150px;
    }
    
    .gauge-value {
      position: absolute;
      font-size: 24px;
      font-weight: bold;
      color: #333;
      
      .gauge-unit {
        font-size: 12px;
        font-weight: normal;
        color: #999;
      }
    }
  }
}

// 温控器样式
.thermostat-widget {
  width: 100%;
  height: 100%;
  padding: 16px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  border-radius: 50%;
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  
  .thermostat-title {
    font-size: 12px;
    opacity: 0.8;
    margin-bottom: 8px;
  }
  
  .thermostat-display {
    text-align: center;
    
    .current-temp {
      font-size: 36px;
      font-weight: bold;
      line-height: 1;
    }
    
    .set-temp {
      display: flex;
      align-items: center;
      gap: 8px;
      margin-top: 8px;
      font-size: 14px;
    }
  }
}

// 风扇控制样式
.fan-control-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  .fan-title {
    font-size: 14px;
    color: #333;
  }
  
  .fan-icon {
    font-size: 32px;
    color: #1890ff;
    
    &.spinning {
      animation: spin 1s linear infinite;
    }
  }
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

// 闸机控制样式
.gate-control-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  
  .gate-icon {
    font-size: 32px;
    color: #1890ff;
  }
  
  .gate-info {
    flex: 1;
    
    .gate-title {
      font-size: 14px;
      font-weight: 500;
    }
    
    .gate-status {
      font-size: 12px;
      color: #666;
    }
  }
  
  .gate-actions {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
}

// 摄像头样式
.camera-widget {
  width: 100%;
  height: 100%;
  background: #000;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  
  .camera-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 8px 12px;
    background: rgba(0, 0, 0, 0.5);
    color: #fff;
    font-size: 12px;
    
    .camera-status {
      color: #f5222d;
      
      &.online {
        color: #52c41a;
      }
    }
  }
  
  .camera-preview {
    flex: 1;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    color: #666;
    font-size: 48px;
    
    span {
      font-size: 12px;
      margin-top: 8px;
    }
  }
  
  .camera-controls {
    padding: 8px;
    background: rgba(0, 0, 0, 0.5);
    display: flex;
    justify-content: center;
    gap: 8px;
  }
}

// 报警器样式
.alarm-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  
  &.triggered {
    background: #fff1f0;
    animation: alarm-flash 0.5s infinite;
  }
  
  .alarm-icon {
    font-size: 28px;
    color: #faad14;
  }
  
  .alarm-info {
    flex: 1;
    
    .alarm-title {
      font-size: 14px;
      font-weight: 500;
    }
    
    .alarm-status {
      font-size: 12px;
      color: #666;
    }
  }
  
  .alarm-actions {
    display: flex;
    flex-direction: column;
    gap: 4px;
  }
}

@keyframes alarm-flash {
  0%, 100% { background: #fff1f0; }
  50% { background: #ffccc7; }
}

// 空气质量样式
.air-quality-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  border-radius: 8px;
  
  .aq-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .aq-main {
    text-align: center;
    
    .aq-aqi {
      font-size: 36px;
      font-weight: bold;
      
      .aq-level {
        display: block;
        font-size: 14px;
        font-weight: normal;
      }
    }
  }
  
  .aq-details {
    display: flex;
    justify-content: space-around;
    margin-top: 8px;
    
    .aq-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      font-size: 12px;
      
      span:first-child {
        color: #666;
      }
      
      span:last-child {
        font-weight: 500;
      }
    }
  }
}

// 电表样式
.power-meter-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
  border-radius: 8px;
  
  .pm-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .pm-main {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    
    .pm-icon {
      font-size: 24px;
      color: #fa8c16;
    }
    
    .pm-value {
      font-size: 28px;
      font-weight: bold;
      
      span {
        font-size: 14px;
        font-weight: normal;
      }
    }
  }
  
  .pm-details {
    display: flex;
    justify-content: space-between;
    
    .pm-item {
      font-size: 12px;
      
      span:first-child {
        color: #666;
        margin-right: 4px;
      }
    }
  }
}

// 水表样式
.water-meter-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  background: linear-gradient(135deg, #a1c4fd 0%, #c2e9fb 100%);
  border-radius: 8px;
  
  .wm-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .wm-main {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-bottom: 8px;
    
    .wm-icon {
      font-size: 24px;
      color: #1890ff;
    }
    
    .wm-value {
      font-size: 28px;
      font-weight: bold;
      
      span {
        font-size: 14px;
        font-weight: normal;
      }
    }
  }
  
  .wm-total {
    font-size: 12px;
    color: #666;
  }
}

// 人体感应样式
.occupancy-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: #f5f5f5;
  border-radius: 8px;
  
  &.occupied {
    background: #e6f7ff;
    
    .occupancy-icon {
      color: #1890ff;
    }
  }
  
  .occupancy-icon {
    font-size: 28px;
    color: #999;
  }
  
  .occupancy-info {
    .occupancy-title {
      font-size: 14px;
      font-weight: 500;
    }
    
    .occupancy-status {
      font-size: 12px;
      color: #666;
    }
  }
}

// 光照传感器样式
.light-sensor-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  display: flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #fff9c4 0%, #ffecb3 100%);
  border-radius: 8px;
  
  .ls-icon {
    font-size: 28px;
    color: #faad14;
  }
  
  .ls-info {
    .ls-title {
      font-size: 14px;
      font-weight: 500;
    }
    
    .ls-value {
      font-size: 18px;
      font-weight: bold;
      color: #fa8c16;
    }
  }
}

// 色温/颜色样式
.color-picker-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  
  .cp-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 8px;
  }
  
  .cp-temp {
    display: flex;
    align-items: center;
    gap: 8px;
    
    :deep(.ant-slider) {
      flex: 1;
    }
    
    .cp-value {
      min-width: 60px;
      text-align: right;
      font-size: 12px;
      color: #666;
    }
  }
  
  .cp-color {
    input[type="color"] {
      width: 100%;
      height: 40px;
      border: none;
      border-radius: 4px;
      cursor: pointer;
    }
  }
}

// 灯光场景样式
.light-scene-widget {
  width: 100%;
  height: 100%;
  padding: 12px;
  
  .ls-title {
    font-size: 14px;
    font-weight: 500;
    margin-bottom: 12px;
  }
  
  .ls-scenes {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 8px;
    
    .ls-scene-item {
      display: flex;
      flex-direction: column;
      align-items: center;
      padding: 12px 8px;
      background: #f5f5f5;
      border-radius: 8px;
      cursor: pointer;
      transition: all 0.2s;
      
      &:hover {
        background: #e6f7ff;
      }
      
      &.active {
        background: #1890ff;
        color: #fff;
      }
      
      span {
        font-size: 12px;
        margin-top: 4px;
      }
    }
  }
}

// 图标样式
.icon-widget {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}
</style>
