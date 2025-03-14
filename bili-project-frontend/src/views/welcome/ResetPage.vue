<script setup>
import {computed, reactive, ref} from "vue";
import {EditPen, Lock, Message} from "@element-plus/icons-vue";
import {ElMessage} from "element-plus";
import {get, post} from "@/net";
import router from "@/router";

const active = ref(0)
const coldTime = ref(0)
const formRef = ref()
const isEmailValid = computed(() => /^[\w.-]+@[\w.-]+\.\w+$/.test(form.email))


const form = reactive({
  email: '',
  code: '',
  password: '',
  password_repeat: ''
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

const rule = {
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
    get(`/api/auth/ask-code?email=${form.email}&type=reset`, () => {
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

function confirmReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/reset-confirm', {
        email: form.email,
        code: form.code
      }, () => active.value++)
    }
  })
}

function doReset() {
  formRef.value.validate((valid) => {
    if (valid) {
      post('/api/auth/reset-password', {...form}, () => {
        ElMessage.success("密码重置成功，请重新登录")
        router.push('/')
      })
    }
  })
}

</script>

<template>
  <div style="text-align: center">
    <div style="margin-top: 30px">
      <el-steps :active="active" align-center finish-status="success">
        <el-step title="验证电子邮箱"/>
        <el-step title="重新设定密码"/>
      </el-steps>
    </div>
    <div v-if="active === 0" style="margin: 0 20px;">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请输入需要重置密码的电子邮件地址</div>
      </div>
      <div style="margin-top: 50px">
        <el-form ref="formRef" :model="form" :rules="rule">
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
        <el-button plain style="width: 80%" type="warning" @click="confirmReset">开始重置密码</el-button>
      </div>

    </div>
    <div v-if="active === 1" style="margin: 0 20px;">
      <div style="margin-top: 80px">
        <div style="font-size: 25px;font-weight: bold">重置密码</div>
        <div style="font-size: 14px;color: grey">请填写你的新密码，务必牢记，防止丢失</div>
      </div>
      <div style="margin-top: 50px">
        <el-form ref="formRef" :model="form" :rules="rule">
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
        </el-form>
      </div>
      <div style="margin-top: 50px">
        <el-button plain style="width: 80%" type="danger" @click="doReset">立即重置密码</el-button>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>