<template>
  <div id="app">
    <router-view />
  </div>
</template>

<style>
#app {
  min-height: 100vh;
}

/* 清新淡雅的主题色 */
:root {
  --primary-color: #5B8FF9;
  --primary-light: #E6F7FF;
  --success-color: #52C41A;
  --warning-color: #FAAD14;
  --danger-color: #F5222D;
  --text-primary: #262626;
  --text-secondary: #595959;
  --text-muted: #8C8C8C;
  --border-color: #D9D9D9;
  --bg-color: #F5F7FA;
  --card-bg: #FFFFFF;
}

body {
  background-color: var(--bg-color);
  color: var(--text-primary);
}

/* 滚动条样式 */
::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

/* 卡片样式 */
.el-card {
  border-radius: 8px;
  border: none;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

/* 按钮样式 - 增大按钮尺寸 */
.el-button {
  border-radius: 4px;
  padding: 10px 20px;
  font-size: 14px;
}

.el-button--small {
  padding: 8px 15px;
  font-size: 13px;
}

.el-button--mini {
  padding: 6px 12px;
  font-size: 12px;
}

.el-button--text {
  padding: 8px 12px;
  font-size: 14px;
}

.el-button--primary {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.el-button--primary:hover {
  background-color: #4A7DE4;
  border-color: #4A7DE4;
}

/* 表格操作按钮样式 */
.el-table .el-button--text {
  padding: 8px 12px;
  font-size: 14px;
}

/* 表格样式 */
.el-table {
  border-radius: 8px;
}

.el-table th {
  background-color: #FAFAFA !important;
  color: var(--text-primary);
  font-weight: 600;
}

/* 输入框样式 */
.el-input__inner {
  border-radius: 4px;
}

/* 分页样式 */
.el-pagination {
  margin-top: 20px;
  text-align: right;
}

/* 消息提示样式 - 调整位置到底部 */
.el-message {
  top: auto !important;
  bottom: 20px !important;
  min-width: 300px;
  padding: 15px 20px;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.el-message__content {
  font-size: 14px;
}

/* 消息提示成功样式 */
.el-message--success {
  background-color: #f0f9eb;
  border-color: #e1f3d8;
}

/* 消息提示错误样式 */
.el-message--error {
  background-color: #fef0f0;
  border-color: #fde2e2;
}

/* 消息提示警告样式 */
.el-message--warning {
  background-color: #fdf6ec;
  border-color: #faecd8;
}
</style>
