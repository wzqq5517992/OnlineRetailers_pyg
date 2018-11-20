# java自动生成代码

#### 项目介绍
 **项目主要是用于自动生成基础性代码，提交开发效率。** 
此项目是采用JDBC+freemarker技术，按照自定义的模块生成增删改查的基础代码，省去了写基本的controller+model+dto+service+serviceimpl+dao+xml基础代码。

#### 软件架构
- **环境说明**
1. 前端：目前没有开发前端，未来可能会往UI界面方向拉分支
2. 后端：JDBC+freemarker
3. 编译器：eclipse 
4. JDK 1.8 

-  **项目的包结构说明** 
- an.aixuegao
    - common
        - configuration - 系统全局的配置中心，在这里配置生成的文件应用到的工程，生成的需要的基本路径
        - constant      - 系统常量
    - core              - 系统的主入口，要生成基础代码，在MybatisGenerator.java中new GeneratorBuilder(模块名称,指定生成的实体类名        称，表名，业务用途).builder(); 即可。
                           注：这里可以生成多张表的基础代码(如：5张，10张，20张，甚至一个业务模块的代码)
    - dbutil            - 连接数据库的工具包，用于获取数据库连接，获取元数据等。
    - entity            - 实体包，把model,service,dao,mapper等抽象成实体，可以自由封装。方便freemarker生成。
    - support           - 支持包，用于提供主要的支持接口，比如生成接口，后期版本可提供用户自定义生成的接口。
    - utils             
        - common        - 公共工具(好像这里要适当调整一下比较合理，下次调吧^_^)，有string工具和数据库元数据包装工具，jdbcType转换工具
        - freemarker    - freemarker工具包，主要用于获取freemarker模板等配置。
        - generate      - 生成model,service,dao等对应的工具

#### 使用
1. 系统全局的配置中心，配置好要生成的代码的工程和基本包结构
2. 在数据库连接工具包中配置好数据库连接(用户名，密码，库等)
3. 在core包MybatisGenerator中(或者是任意类中) 按照上述的new GeneratorBuilder(各参数) 即可。生成在本工程的指定包路径下，无需再从D盘或者E盘拷贝一次到工程中。更快速简便。
4. 目前只支持根据mysql表名生成代码。
5.  **切记！！！一定要先建好表，才能生成** 

#### 兔子个人网站
    www.aixuegao.cn 

#### 联系方式
    Icey社区交流群(java技术)：696605865 
    作者QQ：3143901851
