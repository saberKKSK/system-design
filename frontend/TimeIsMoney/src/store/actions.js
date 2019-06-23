import * as mutations from './mutations'
import * as userAPI from './../api/user'
import * as sysAPI from './../api/system'
export const SIGN_IN = 'SIGN_IN'
export const SIGN_UP = 'SIGN_UP'
export const MESSAGE = 'MESSAGE'
export const SEND_IDENTIFY = 'SEND_IDENTIFY'
export const CHECK_IDENTIFY = 'CHECK_IDENTIFY'
export const SEND_IDENTIFY2 = 'SEND_IDENTIFY2'
export const CHECK_IDENTIFY2 = 'CHECK_IDENTIFY2'


export default {
    [SIGN_IN]({ commit, state }, info) {
        let res = userAPI.login(info.username, info.password, info.mode)
        return res

    },
    [SIGN_UP]({ commit, state }, info) {
        let res = userAPI.userRegister(info.username, info.password, info.mode)
        return res
    },
    [MESSAGE]({ commit, state }, data) {
        let send = [{
            toId: data.toId,
            fromId: data.fromId,
            hasRead: false,
            title: '',
            content: ''
        }]
        console.log('消息：' + data.type)
        if (data.type === 'fill') {
            send[0].title = '有人填你的问卷啦！'
            send[0].content = '<' + data.fromName + '>已填写问卷' + '《' + data.quesTitle + '》'
            console.log('消息：' + JSON.stringify(send))
        }
        console.log('消息：' + JSON.stringify(send))
        return sysAPI.sendMessage(send)
    },
    [SEND_IDENTIFY]({ commit, state }, info) {
        let res = userAPI.sendIndentify(info.username, info.mode)
        return res
    },
    [CHECK_IDENTIFY]({ commit, state }, info) {
        let res = userAPI.checkIndentify(info.username, info.mode, info.inputCode)
        return res
    },
    [SEND_IDENTIFY2]({ commit, state }, info) {
        let res = userAPI.sendIndentify2(info.username, info.mode)
        return res
    },
    [CHECK_IDENTIFY2]({ commit, state }, info) {
        let res = userAPI.checkIndentify2(info.username, info.mode, info.inputCode)
        return res
    }