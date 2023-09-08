import axios from 'axios'
import {ElMessage} from "element-plus";

const authItemName = "access_token"

const defaultError = (err) => {
    console.error(err)
    ElMessage.warning('发生了一些错误，请联系管理员')
}
const defaultFailure = (message, code, url) => {
    console.warn(`请求地址:${url}, 状态码:${code}, 错误信息:${message}`)
    ElMessage.warning(message)
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
    const authObj = JSON.parse(str)
    if (authObj.expire <= new Date()) {
        deleteAccessToken()
        ElMessage.warning('登录状态已过期，请重新登录')
        return null
    }
    return authObj.token
}

function deleteAccessToken() {
    localStorage.removeItem(authItemName)
    sessionStorage.removeItem(authItemName)
}

function accessHeader() {
    const token = takeAccessToken()
    return token ? {'Authorization': `Bearer ${takeAccessToken()}`} : {}
}

// 封装内部Post调用 包含success failure error回调
function internalPost(url, data, header, success, failure, error = defaultError) {
    axios.post(url, data, {headers: header}).then(({data}) => {
        console.log(data)
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }

    }).catch(err => error(err))
}

function post(url, data, success, failure = defaultFailure) {
    internalPost(url, data, accessHeader(), success, failure)
}

// 封装内部Get调用
function internalGet(url, header, success, failure, error = defaultError) {
    axios.get(url, {headers: header}).then(({data}) => {
        if (data.code === 200) {
            success(data.data)
        } else {
            failure(data.message, data.code, url)
        }

    }).catch(err => error(err))
}

function get(url, success, failure = defaultFailure) {
    internalGet(url, accessHeader(), success, failure)
}

function login(username, password, remember, success, failure = defaultFailure) {
    internalPost('/api/auth/login', {
        username: username, password: password, remember: remember
    }, {
        'Content-Type': 'application/x-www-form-urlencoded'
    }, (data) => {
        storeAccessToken(data.token, remember, data.expire)
        ElMessage.success(`登录成功，欢迎${data.username}来到我们的系统`)
        success(data)
    }, failure)
}


function logout(success, failure = defaultFailure) {
    get('api/auth/logout', () => {
        deleteAccessToken()
        ElMessage.success('退出登录成功，欢迎您再次使用')
        success()
    }, failure)
}

function unauthorized() {
    return !takeAccessToken()
}

export {login, logout, get, post, unauthorized}

