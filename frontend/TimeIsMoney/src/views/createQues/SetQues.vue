<template>
    <div style="width: 100%">
        <Form ref="formValidate" :model="formValidate" :rules="ruleValidate" :label-width="100">
            <FormItem label="简介" prop="detail">
                <Input v-model="formValidate.detail" placeholder="输入问卷简介"></Input>
            </FormItem>
            <FormItem label="要求" prop="command">
                <Input v-model="formValidate.command" placeholder="输入问卷填写者限制，如：大学生"></Input>
            </FormItem>
            <FormItem label="酬金" prop="reward">
                <Input v-model="formValidate.reward" placeholder="输入每份问卷填写报酬"></Input>
            </FormItem>
            <FormItem label="招募数量" prop="quantity">
                <Input v-model="formValidate.quantity" placeholder="输入目标收集数量"></Input>
            </FormItem>
            <FormItem label="开始时间">
                <Row>
                    <Col span="6">
                    <FormItem prop="startdate">
                        <DatePicker type="date" placeholder="选择日期" v-model="formValidate.startdate"></DatePicker>
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem prop="starttime">
                        <TimePicker type="time" placeholder="选择时间" v-model="formValidate.starttime"></TimePicker>
                    </FormItem>
                    </Col>
                </Row>
            </FormItem>
            <FormItem label="结束时间">
                <Row>
                    <Col span="6">
                    <FormItem prop="enddate">
                        <DatePicker type="date" placeholder="选择日期" v-model="formValidate.enddate"></DatePicker>
                    </FormItem>
                    </Col>
                    <Col span="6">
                    <FormItem prop="endtime">
                        <TimePicker type="time" placeholder="选择时间" v-model="formValidate.endtime"></TimePicker>
                    </FormItem>
                    </Col>
                </Row>
            </FormItem>
            <!-- <FormItem label="Gender" prop="gender">
                <RadioGroup v-model="formValidate.gender">
                    <Radio label="male">Male</Radio>
                    <Radio label="female">Female</Radio>
                </RadioGroup>
            </FormItem> -->
            <FormItem label="个人信息展示" prop="info">
                <CheckboxGroup v-model="formValidate.info">
                    <Checkbox label="微信"></Checkbox>
                    <Checkbox label="电话"></Checkbox>
                    <Checkbox label="QQ"></Checkbox>
                    <Checkbox label="个性签名"></Checkbox>
                </CheckboxGroup>
            </FormItem>
            <!-- <FormItem label="Desc" prop="desc">
                <Input v-model="formValidate.desc" type="textarea" :autosize="{minRows: 2,maxRows: 5}" placeholder="Enter something..."></Input>
            </FormItem> -->
            <!--    <FormItem>
                <Button @click="handleReset('formValidate')" style="margin-left: 8px">Reset</Button>
            </FormItem>-->
        </Form>
        <div style="width: 100%; text-align: center; margin-top: 105px">
            <Button @click="changeStep(-1)" style="margin-right:10px">上一步</Button><Button @click="handleSubmit('formValidate')">下一步</Button>
        </div>
    </div>
</template>
<script>
import { mapState } from 'vuex'
import { Ques } from '../../store/questionnaire/index.js'
export default {
    data() {
        return {
            ruleValidate: {
                detail: [
                    { required: false, trigger: 'blur' }
                ],
                command: [
                    { required: false, trigger: 'blur' }
                ],
                quantity: [
                    { required: true, trigger: 'blur' },
                    { type: 'number', message: '输入必须为整数', trigger: 'change', transform(value) { return Number(value); } }
                ],
                reward: [
                    { required: true, message: '报酬不能为空', trigger: 'blur' },
                    { type: 'number', message: '输入必须为整数', trigger: 'blur', transform(value) { return Number(value); } }
                ],
                // gender: [
                //     { required: true, message: 'Please select gender', trigger: 'change' }
                // ],
                info: [
                    { required: true, type: 'array', min: 1, message: '至少选择显示一个联系方式', trigger: 'change' },
                    { type: 'array', trigger: 'change' }
                ],
                startdate: [
                    { required: true, type: 'date', message: 'Please select the date', trigger: 'change' }
                ],
                starttime: [
                    { required: true, type: 'string', message: 'Please select time', trigger: 'change' }
                ],
                enddate: [
                    { required: true, type: 'date', message: 'Please select the date', trigger: 'change' }
                ],
                endtime: [
                    { required: true, type: 'string', message: 'Please select time', trigger: 'change' }
                ],
                // desc: [
                //     { required: true, message: 'Please enter a personal introduction', trigger: 'blur' },
                //     { type: 'string', min: 20, message: 'Introduce no less than 20 words', trigger: 'blur' }
                // ]
            },
            formValidate: {
                title: '1111',
                detail: '',
                command: '',
                reward: 0,
                // gender: '',
                quantity: 0,
                info: [],
                startdate: '',
                starttime: '',
                enddate: '',
                endtime: '',
                number: 0
                // desc: ''
            },
        }

    },
    methods: {
        handleSubmit(name) {
            this.$refs[name].validate((valid) => {
                if (valid) {
                    this.$store.commit('Ques/createQues/SET_VALIDATE', this.formValidate)
                    // this.$Message.success('发布成功!');
                    // this.$router.push('questionnaire');
                    //("shezhi: " + JSON.stringify(this.formValidate))
                    this.$emit('changeStep', 1)
                } else {
                    this.$Message.error('设置失败！请完善信息后再次尝试');
                }
            })
        },
        handleReset(name) {
            this.$refs[name].resetFields();
        },
        changeStep: function(step) {
            // //()
            this.$emit('changeStep', step)
        }
    }
}
</script>
<style>
</style>