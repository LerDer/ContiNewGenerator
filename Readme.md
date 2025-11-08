# 简单说明

<a href="https://gitcode.com/continew/continew-admin">ContiNew Admin</a> Code Generator

Version Support: 3.5.0 | 3.6.0 | 3.7.0 | 4.0.0

Database Support: MySQL, PostgreSQL

Automatic parsing of database configuration information, connecting to the database

Configurable output directories (front-end & back-end)

Generates complete code to specified paths

Uses ContiNew Admin template standards

Direct code generation with one-click operation
    

---

为<a href="https://gitcode.com/continew/continew-admin">ContiNew Admin</a>量身定制的代码生成器

支持3.5.0、3.6.0、3.7.0、4.0.0版本。

支持MySQL和PostgreSQL数据库。

根据配置文件自动解析数据库配置信息, 连接数据库

设置前端, 后端目录后, 一键生成前端, 后端代码到指定目录,
生成的代码采用 ContiNew Admin 项目的代码生成器模板, 生成后可直接使用

# 使用方式

1. 选择后端项目路径（src目录上一层，就是项目名称的目录）
2. 选择前端项目路径（同样src上一层目录，就是项目名称的目录），会自动解析src下views目录下的目录作为模块
3. 选择配置文件，一般是application-dev.yml，会自动解析数据库配置，刷新表名下拉框
4. 选择包名，推荐选择最顶层的包，选择之后，也可以在文本框编辑，生成的后端代码会在这个包下面各自创建自己的包
5. 输入作者名称
6. 可以选择是否启用覆盖模式，DO是否使用Continew提供的基类，Controller是否使用Continew提供的API注解接口，Service是否使用原Mybatis-Plus的接口
7. 选择代码模板版本
8. 选择所属模块，模块名会作为接口地址的一部分，也影响前端代码的位置
9. 输入表面前缀，如果表名是下划线连接的，会自动解析，回显最前面的部分
10. 输入业务名称，如果设置了表的注释，也会自动回显出来
11. 点击下一步，会打开表属性调整窗口
12. 可以修改字段的名称，Java类型，描述等要素信息
13. 点击预览，可以预览最终生成代码的样子
14. 在预览页面，可以点击生成，直接生成代码，支持多选
15. 也可以关闭预览窗口，在属性调整窗口点击生成，生成代码

# 特点

1. 支持3.5.0、3.6.0、3.7.0、4.0.0版本
2. 支持MySQL和PostgreSQL数据库
3. 根据配置文件自动解析数据库配置信息, 连接数据库，刷新表选择下拉框
4. 支持记忆表属性调整结果，再次打开可以显示上次修改的结果

# 技术点

1. Action创建
2. 自定义UI组件，列表渲染，表格渲染，Icon工具
3. 配置持久化，添加配置菜单
4. 策略模式，分别处理 MySQL、PostgreSQL 数据库 查询表信息，列信息
5. 自定义编辑器
6. 调用Idea代码格式化工具
7. 文件选择工具
8. 提示工具
9. 策略模式管理代码模板
10. 自定义图标


# 捐赠

如果您觉得此插件对您有帮助，节省了您的时间，提高了您的效率，请支持作者，期待您的捐赠。

![](src/main/resources/icons/wx.jpg)

![](src/main/resources/icons/zfb.jpg)
