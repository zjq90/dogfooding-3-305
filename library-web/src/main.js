import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import * as echarts from 'echarts'

Vue.use(ElementUI, {
  size: 'medium'
})

// 全局配置Message的offset和duration
const originMessage = Vue.prototype.$message
const messageConfig = { offset: 100, duration: 3000 }

// 重写$message，确保所有调用都使用统一的偏移量
Vue.prototype.$message = function(options) {
  if (typeof options === 'string') {
    return originMessage({
      message: options,
      ...messageConfig
    })
  }
  return originMessage({
    ...messageConfig,
    ...options
  })
}

// 重写所有快捷方法
['success', 'error', 'warning', 'info'].forEach(type => {
  Vue.prototype.$message[type] = function(message, options = {}) {
    return originMessage[type]({
      message,
      ...messageConfig,
      ...options
    })
  }
})

Vue.prototype.$echarts = echarts

Vue.config.productionTip = false

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
