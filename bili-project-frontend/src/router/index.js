import {createRouter, createWebHistory} from "vue-router";
import {unauthorized} from "../net";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/views/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/views/welcome/RegisterPage.vue')
                }

            ]
        },
        {
            path: '/index',
            name: 'index',
            component: () => import('@/views/IndexView.vue')
        }
    ]
})

// 配置路由守卫，在前端防止用户未登录状态访问其他页面
router.beforeEach((to, from, next) => {
    const isUnauthorized = unauthorized()
    // 已经登录了又去访问登录页面
    if (to.name.startsWith('welcome-') && !isUnauthorized) {
        next('/index')
    } else if (to.fullPath.startsWith('/index') && isUnauthorized) {
        // 未登录情况去访问
        // 打回去
        next('/')
    } else {
        next()
    }
})

export default router
