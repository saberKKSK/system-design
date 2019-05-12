import * as mutations from './mutations'
import * as quesAPI from './../../api/question.js'

export const GET_QUESLIST = 'GET_QUESLIST'
export const GET_ADVERTISES = 'GET_ADVERTISES'
export const GET_DETAIL = 'GET_DETAIL'
// export const GET_RANKLIST = 'GET_RANKLIST'

export default {

  [GET_QUESLIST] ({commit}) {
    quesAPI.getQuesList().then(
      (info) => {
        if(info.success)
          commit(mutations.SET_QUESLIST, info.data)
        else
          console.log('error:'+JSON.stringify(info))
      }
    )
    
    
  },

  [GET_DETAIL] ({dispatch, commit}, id) {
    // console.log('123456789')
    quesAPI.getDetail(id).then(
      (info) => {
        commit(mutations.SET_DETAIL, info)
      }
    )
    // console.log(data)
    
  },

}