import { defHttp } from '/@/utils/http/axios';
import { useMessage } from '/@/hooks/web/useMessage';

const { createMessage } = useMessage();

enum Api {
  // 面板管理
  panelList = '/configuration/panel/list',
  panelAdd = '/configuration/panel/add',
  panelEdit = '/configuration/panel/edit',
  panelDelete = '/configuration/panel/delete',
  panelQueryById = '/configuration/panel/queryById',
  
  // 页面管理
  pageList = '/configuration/panelPage/list',
  pageAdd = '/configuration/panelPage/add',
  pageEdit = '/configuration/panelPage/edit',
  pageDelete = '/configuration/panelPage/delete',
  pageQueryById = '/configuration/panelPage/queryById',
  
  // 版本管理
  versionList = '/configuration/panelPageVersion/list',
  versionSave = '/configuration/panelPageVersion/save',
  versionGet = '/configuration/panelPageVersion/get',
  versionPublish = '/configuration/panelPageVersion/publish',
  
  // 运行态
  runtimeGetPublished = '/configuration/panelRuntime/getPublished',
  runtimeGetVersion = '/configuration/panelRuntime/getVersion',
  
  // IoT命令
  iotCommand = '/iot/command',
  iotShadowSnapshot = '/iot/shadow/snapshot',
}

// ========== 面板管理 ==========
export const getPanelList = (params) => defHttp.get({ url: Api.panelList, params });

export const addPanel = (params) => {
  return defHttp.post({ url: Api.panelAdd, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '添加成功');
    } else {
      createMessage.error(res.message || '添加失败');
    }
    return res;
  });
};

export const editPanel = (params) => {
  return defHttp.put({ url: Api.panelEdit, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '编辑成功');
    } else {
      createMessage.error(res.message || '编辑失败');
    }
    return res;
  });
};

export const deletePanel = (params, handleSuccess?) => {
  return defHttp.delete({ url: Api.panelDelete, params }, { isTransformResponse: false, joinParamsToUrl: true }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '删除成功');
      handleSuccess && handleSuccess();
    } else {
      createMessage.error(res.message || '删除失败');
    }
    return res;
  });
};

export const getPanelById = (id: string) => defHttp.get({ url: Api.panelQueryById, params: { id } });

// ========== 页面管理 ==========
export const getPageList = (params) => defHttp.get({ url: Api.pageList, params });

export const addPage = (params) => {
  return defHttp.post({ url: Api.pageAdd, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '添加成功');
    } else {
      createMessage.error(res.message || '添加失败');
    }
    return res;
  });
};

export const editPage = (params) => {
  return defHttp.put({ url: Api.pageEdit, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '编辑成功');
    } else {
      createMessage.error(res.message || '编辑失败');
    }
    return res;
  });
};

export const deletePage = (params, handleSuccess?) => {
  return defHttp.delete({ url: Api.pageDelete, params }, { isTransformResponse: false, joinParamsToUrl: true }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '删除成功');
      handleSuccess && handleSuccess();
    } else {
      createMessage.error(res.message || '删除失败');
    }
    return res;
  });
};

export const getPageById = (id: string) => defHttp.get({ url: Api.pageQueryById, params: { id } });

// ========== 版本管理 ==========
export const getVersionList = (params) => defHttp.get({ url: Api.versionList, params });

export const saveVersion = (params) => {
  return defHttp.post({ url: Api.versionSave, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success('保存成功');
    } else {
      createMessage.error(res.message || '保存失败');
    }
    return res;
  });
};

export const getVersionById = (id: string) => defHttp.get({ url: Api.versionGet, params: { id } });

export const publishVersion = (params) => {
  return defHttp.post({ url: Api.versionPublish, params }, { isTransformResponse: false }).then((res) => {
    if (res.success) {
      createMessage.success(res.message || '发布成功');
    } else {
      createMessage.error(res.message || '发布失败');
    }
    return res;
  });
};

// ========== 运行态 ==========
export const getPublishedConfig = (pageId: string) => 
  defHttp.get({ url: Api.runtimeGetPublished, params: { pageId } });

export const getVersionConfig = (pageId: string, versionId: string) => 
  defHttp.get({ url: Api.runtimeGetVersion, params: { pageId, versionId } });

// ========== IoT命令 ==========
export const sendIotCommand = (params) => {
  return defHttp.post({ url: Api.iotCommand, params }, { isTransformResponse: false });
};

export const getShadowSnapshot = (deviceIds: string[]) => 
  defHttp.get({ url: Api.iotShadowSnapshot, params: { deviceIds: deviceIds.join(',') } });
