<template>
    <div class="publishDiv">
        <div style="width:100%;">
            <img style="width:100%; height:180px;" src="../images/personal/background.png" />
        </div>
        <div style="margin: 0 10%;">
            <div style="margin-bottom:20px;">
                <h2 style="font-size:25px;color:#CE4747;margin-top:20px; float: left; vertical-align:middle;">我发布的问卷 </h2>
                <p style="font-size:20px;color:#CE4747;margin-top:25px; margin-left:10px; float: left; vertical-align:middle;">Publish</p>
                <div style="clear:both" />
            </div>
            <div v-if="!noQues" style="width:100%;margin-top:5px;">
                <div v-for="(ques,index) in sortPublished">
                    <div>
                        <span id="dynamicDate" style="font-size:15px;color:#ce4545;vertical-align:middle;"> ● {{ques.Infos.createTime}} </span> <span style="font-size:15px;vertical-align:middle;color:gray;">我发布了</span>
                    </div>
                    <task :data="ques" :key="index" type="1" mode="0" @click.native="getDetail(ques.quesID)"></task>
                </div>
            </div>
            <div v-else style="width:100%;margin-top:20px;">
                <div style=" margin:0 auto;width:500px; font-size:25px;text-align:center;">
                    <span style="color:#CE4747;"> 您暂时没有发布的问卷噢 </span>
                </div>
                <div style=" text-align: center;">
                    <img src="../../static/noPublish.png" style="width:900px;height:500px" />
                </div>
            </div>
        </div>
        <detail :showDetail="detailModel" :index="index"></detail>
    </div>
</template>
<script>
import { mapState } from 'vuex'
import { Personal } from '../store/personal/index.js'
import { Ques } from '../store/questionnaire/index.js'
import task from './components/Task.vue'
import detail from "./components/Detail.vue"
import service from './../util/service.js'

export default {
    components: {
        task,
        detail
    },
    computed: mapState('Personal', {
        publishLists: 'publishing',
        attendLists: 'attending',
        collectLists: 'starring',
        detailContent: 'quesDetail'
    }),


    data() {
        return {
            detailModel: false,
            index: 0,
            sortPublished: [],
            noQues: false
        }

    },
    methods: {
        getDetail(id) {
            this.$store.dispatch('Ques/GET_DETAIL', id)
            this.detailModel = !this.detailModel
        },
        sortBykey(ary, key1, key2) {
            return ary.sort(function(a, b) {
                let x = a[key1][key2]
                let y = b[key1][key2]
                return ((x < y) ? -1 : (x > y) ? 1 : 0)
            })
        },
        hey() {
            alert("hey!");
        }
    },
    mounted() {

        this.$store.dispatch('Personal/GET_PUBLISH'); //分发action
        var _this = this;
        setTimeout(function() { //注意在函数里面再使用this，此时this指向函数
            _this.sortPublished = _this.sortBykey(_this.$store.state.Personal.publishing, 'Infos', 'createTime');
            //(_this.$store.state)
            //(_this.sortPublished.length)
            if (_this.sortPublished.length === 0) {
                _this.noQues = true;
            }
            //_this.hey();
        }, 1000);


    }
}
</script>
<style>
h1 {
    height: 150px;

    img {
        height: 100%;
    }
}

h2 {
    color: #666;
    margin-bottom: 200px;

    p {
        margin: 0 0 50px;
    }
}


.ivu-row-flex {
    height: 100%;
}

.ivu-tabs-content,
.ivu-tabs-content-animated {
    height: 450px;
}

Row {
    margin: 30px;
}

.ivu-tabs-nav-container {
    font-size: 30px !important;
}

.personal .ivu-tabs-nav-container {
    font-size: 22px !important;
}
</style>