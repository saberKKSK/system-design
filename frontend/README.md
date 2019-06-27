# T.I.M. 前端部署使用说明

本项目前端环境包括:

- Vue.js 2 
- vue-router 
- webpack2
- iView 3
- vuex
- animate
- axios
- vue-cookies

## 项目在线运行

项目部署地址：[https://timoney.xyz/](https://timoney.xyz/)

直接在线打开使用

## 项目本地运行

进入路径[system-design/frontend/TimeIsMoney]

- ！需要安装python

- ！当前目录下具有dist文件夹

执行

    python -m http.server 5678

在浏览器输入localhost:5678即可运行项目




## 开发环境及部署（DEV）

下面的指令均在路径`system-design/frontend/TimeIsMoney`下执行

### iview环境安装

	npm install iview --save


### 项目安装

	npm install

### 安装animate库

	npm install animate.css --save

### 安装vuex模块

	npm install vuex --save

### 安装axios模块

	npm install vue-axios --save

### 安装vue-cookies模块

	npm install vue-cookies --save

### 修改样式库

1. 把当前文件夹内的iview.css和node_modules->ivew->dist->styles下ivew.css替换·
2. 将fonts文件夹中的【ionicons.woff2】文件放入node_modules->ivew->dist->styles->fonts文件夹中（也可以整个文件夹直接替换）

## 编译打包代码

编译指令

	npm run build

完成后当前目录下生成dist文件夹，里面是编译打包后生成的文件


