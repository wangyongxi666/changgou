package com.changgou.web.seckill.aspect;

import java.lang.annotation.*;

@Inherited
@Documented
@Target({ElementType.FIELD,ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME) //不就能保存到class文件中，并且jvm加载class之后，改注解仍然存在
public @interface AccessLimit {
}
