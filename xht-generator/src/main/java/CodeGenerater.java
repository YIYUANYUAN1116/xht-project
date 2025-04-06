import com.baomidou.mybatisplus.core.exceptions.MybatisPlusException;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.xht.model.entity.base.BaseEntity;
import org.springframework.util.StringUtils;


import java.util.Collections;
import java.util.Scanner;

/**
 * @author : YIYUANYUAN
 * @description : 代码生成器
 * @date: 2023/12/24  11:22
 */
public class CodeGenerater {

    public static String scanner(String tip) {
        Scanner scanner = new Scanner(System.in);
        StringBuilder help = new StringBuilder();
        help.append("请输入" + tip + "：");
        System.out.println(help.toString());
        if (scanner.hasNext()) {
            String ipt = scanner.next();
            if (StringUtils.hasLength(ipt)) {
                return ipt;
            }
        }
        throw new MybatisPlusException("请输入正确的" + tip + "！");
    }


    public static void main(String[] args) throws Exception {

        FastAutoGenerator.create("jdbc:mysql://192.168.213.139:3306/xht_project?serverTimezone=GMT%2B8&characterEncoding=utf-8&useSSL=false&allowPublicKeyRetrieval=true", "root", "yzd2021")
                // 全局配置
                .globalConfig(builder -> {
                    builder.author("xht") // 设置作者
                            .commentDate("yyyy-MM-dd")   //注释日期
                            .outputDir(System.getProperty("user.dir") + "/xht-generator/src/main/java") // 指定输出目录
                            .disableOpenDir() //禁止打开输出目录，默认打开
                    ;
                })
                // 包配置
                .packageConfig(builder -> {
                    builder.parent("com.xht.generator") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/xht-generator/src/main/resources/mappers")); // 设置mapperXml生成路径
                })
                // 策略配置
                .strategyConfig(builder -> {
                    builder.addInclude(scanner("表名，多个英文逗号分割").split(",")) // 设置需要生成的表名
                            // Entity 策略配置
                            .entityBuilder()
                            .enableLombok() //开启 Lombok
                            .enableFileOverride() // 覆盖已生成文件
                            .naming(NamingStrategy.underline_to_camel)  //数据库表映射到实体的命名策略：下划线转驼峰命
                            .columnNaming(NamingStrategy.underline_to_camel)
                            .superClass(BaseEntity.class)
                            .addSuperEntityColumns("id","createTime","updateTime","isDeleted")
                            //数据库表字段映射到实体的命名策略：下划线转驼峰命
                            // Mapper 策略配置
                            .mapperBuilder()
                            .enableFileOverride()
                            .enableBaseColumnList()
                            .enableBaseResultMap()// 覆盖已生成文件
                            // Service 策略配置
                            .serviceBuilder()
                            .enableFileOverride() // 覆盖已生成文件
                            .formatServiceFileName("%sService") //格式化 service 接口文件名称，%s进行匹配表名，如 UserService
                            .formatServiceImplFileName("%sServiceImpl") //格式化 service 实现类文件名称，%s进行匹配表名，如 UserServiceImpl
                            // Controller 策略配置
                            .controllerBuilder()
                            .enableRestStyle()
                            .enableFileOverride() // 覆盖已生成文件
                    ;
                })
                .execute();
    }
}
