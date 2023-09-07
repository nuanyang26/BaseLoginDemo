import axios from 'axios'
import {ElMessage} from "element-plus";

const authItemName = "access_token"

const defaultFailure = (message, code) => {
    console.warn(`请求地址:${url}, 状态码:${code}, 错误信息:${message}`)
    ElMessage.warning(message)
}

const defaultError = (err) => {
    console.error(err)
    ElMessage.warning('发生了一些错误，请联系管理员')
}

function storeAccessToken(token, remember, expire) {
    const authObj = {token: token, expire: expire}
    const str = JSON.stringify(authObj)
    if (remember)
        localStorage.setItem(authItemName, str)
    else
        sessionStorage.setItem(authItemName, str)
}

function takeAccessToken() {
    const str = localStorage.getItem(authItemName) || sessionStorage.getItem(authItemName)
    if (!str) return null;
    const authObj = JSON.stringify(str)
    if (authObj.expire <= new Date()) {
        deleteAccessToken()
        ElMessage.warning('登录状态已过期，请重新登录')
    }
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}


// 封装内部Post调用 包含success failure error回调
function internalPost(url, data, header, success, failure, error) {
    axios.post(url, data, {headers: header}).then(({response}) => {
        if (response.code === 200) {
            success(response.data)
        } else {
            failure(response.message, response.code, url)
        }

    }).catch(err => error(err))
}

// 封装内部Get调用
function internalGet(url, success, failure, error) {
    axios.post(url, data, {headers: header}).then(({response}) => {
        if (response.code === 200) {
            success(response.data)
        } else {
            failure(response.message, response.code, url)
        }

    }).catch(err => error(err))
}

function login(username, password, remember, success, failure = defaultFailure) {
    internalPost('/api/auth/login', {
            username: username,
            password: password,
            remember: remember
        }, {
            'Content--Type': 'application/x-www-form-urlencoded'
        },
        (response) => {
            storeAccessToken(remember, response.token, response.expire)
            ElMessage(`登录成功，欢迎${response.username}来到我们的系统`)
            success(response)
        }, failure)
}


export {login}
