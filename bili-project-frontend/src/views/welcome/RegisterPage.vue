<script setup>
import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message, User} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";

const coldTime = ref(0)
const isEmailValid = computed(() => /^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))

const formRef = ref()


const form = reactive({
  username: '',
  password: '',
  password_repeat: '',
  email: '',
  code: ''
})

const validatePassWord_repeat = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请再次输入密码'))
  } else if (value !== form.password) {
    callback(new Error('两次输入密码不一致!'))
  } else {
    callback()
  }
}

const validateUsername = (rule, value, callback) => {
  if (value === '') {
    callback(new Error('请输入用户名'))
  } else if (!/^[a-zA-Z0-9\u4e00-\u9fa5]+$/.test(value)) {
    callback(new Error('用户名不能包含特殊字符，只能是中/英文'))
  } else {
    callback()
  }
}

const rule = {
  username: [
    {required: true, validator: validateUsername, trigger: ['blur', 'change']},
    {min: 1, max: 10, message: '用户名长度在1-10个字符之间', trigger: ['blur', 'change']},
  ],
  password: [
    {required: true, message: '请输入密码'},
    {min: 6, max: 20, message: '密码长度在6-20个字符之间', trigger: ['blur', 'change']}
  ],
  password_repeat: [
    {required: true, validator: validatePassWord_repeat, trigger: ['blur', 'change']}
  ],
  email: [
    {required: true, message: '请输入邮件地址'},
    {type: 'email', message: '请输入合法的电子邮件地址', trigger: ['blur', 'change']}
  ],
  code: [
    {required: true, message: '请输入验证码'},
    {min: 6, max: 6, message: '验证码为6位', trigger: ['blur', 'change']},
  ]
}

function askCode() {
  if (isEmailValid) {
    coldTime.value = 60;
    get(`/api/auth/ask-code?email=${form.email}&type=register`, () => {
      ElMessage.success(`验证码已发送到邮箱：${form.email}，请注意查收`)
      const handle = setInterval(() => {
        coldTime.value--
        if (coldTime.value === 0) {
          clearInterval(handle)
        }
      }, 1000)
    }, (message) => {
      ElMessage.warning(message)
      coldTime.value = 0
    })
  } else {
    ElMessage.warning('请输入正确的电子邮件')
  }
}

function register() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/register', {...form}, () => {
        ElMessage.success('注册成功，欢迎加入我们')
        router.push('/')
      })
    } else {
      ElMessage.warning('请完整填写表单内容')
    }
  })
}

</script>

<template>
  <div style="text-align: center; margin: 0 20px">
    <div style="margin-top: 150px">
      <div style="font-size: 25px; font-weight: bold">欢迎注册新用户</div>
      <div style="font-size: 14px; color: gray">欢迎注册我们的学习平台，请在下方填写相关信息</div>
    </div>
    <div style="margin-top: 50px">
      <el-form ref="formRef" :model="form" :rules="rule">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="用户名/邮箱账号" type="text">
            <template #prefix>
              <el-icon>
                <User/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" placeholder="密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="password_repeat">
          <el-input v-model="form.password_repeat" placeholder="重复密码" type="password">
            <template #prefix>
              <el-icon>
                <Lock/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="email">
          <el-input v-model="form.email" placeholder="电子邮件地址" type="email">
            <template #prefix>
              <el-icon>
                <Message/>
              </el-icon>
            </template>
          </el-input>
        </el-form-item>
        <el-form-item prop="code">
          <el-row :gutter="10" style="width: 100%;">
            <el-col :span="17">
              <el-input v-model="form.code" placeholder="请输入验证码" type="text">
                <template #prefix>
                  <el-icon>
                    <EditPen/>
                  </el-icon>
                </template>
              </el-input>
            </el-col>
            <el-col :span="5">
              <el-button :disabled="!isEmailValid || coldTime > 0" plain type="success" @click="askCode">
                {{ coldTime > 0 ? `请稍后${coldTime}秒` : '获取验证码' }}
              </el-button>
            </el-col>
          </el-row>
        </el-form-item>

      </el-form>
    </div>
    <div style="margin-top: 50px">
      <el-button plain style="width: 80%" type="warning" @click="register">立即注册</el-button>
    </div>
    <div style="margin-top: 20px">
      <span style="font-size: 14px; line-height: 15px; color:grey;">已有账号？</span>
      <el-link style="translate: 0 -2px" @click="router.push('/')">立即登录</el-link>
    </div>
  </div>

</template>

<style scoped>

</style>