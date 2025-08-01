[Controller.ftl](Controller.ftl)

```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.controller;

- public class ${className} extends BaseController<${classNamePrefix}Service, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> {}

+ public class ${className}Controller extends BaseController<${classNamePrefix}Service, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> {}

```


[DetailResp.ftl](DetailResp.ftl)

```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.model.resp;

- public class ${className} extends BaseDetailResp {
+ public class ${className}DetailResp extends BaseDetailResp {

```

[Entity.ftl](Entity.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.model.entity;

- public class ${className} extends BaseDO {
+ public class ${className}DO extends BaseDO {

+ <#if doExcludeFields?seq_contains(fieldConfig.fieldName)>
+     <#continue>
+ </#if>

-   * ${fieldConfig.comment}
+   * ${fieldConfig.comment!""}

```

[Mapper.ftl](Mapper.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.mapper;

- public interface ${className} extends BaseMapper<${classNamePrefix}DO> {}
+ public interface ${className}Mapper extends BaseMapper<${classNamePrefix}DO> {}

```

[MapperXml.ftl](MapperXml.ftl)
```diff
```

[Menu.ftl](Menu.ftl)
```diff
```

[Query.ftl](Query.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.model.query;

- public class ${className} implements Serializable {
+ public class ${className}Query implements Serializable {

```

[Req.ftl](Req.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.model.req;

- public class ${className} implements Serializable {
+ public class ${className}Req implements Serializable {


- @Length(max = ${fieldConfig.columnSize?c}, message = "${fieldConfig.comment}长度不能超过 {max} 个字符")
+ @Length(max = ${fieldConfig.columnSize?c}, message = "${fieldConfig.comment}长度不能超过 ${fieldConfig.columnSize?c} 个字符")

```

[Resp.ftl](Resp.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.model.resp;

- public class ${className} extends BaseResp {
+ public class ${className}Resp extends BaseResp {


+ <#if respExcludeFields?seq_contains(fieldConfig.fieldName)>
+  <#continue>
+ </#if>

```

[Service.ftl](Service.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.service;

- public interface ${className} extends BaseService<${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req>, IService<${classNamePrefix}DO> {}
+ public interface ${className}Service extends BaseService<${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req>, IService<${classNamePrefix}DO>  {}

```

[ServiceImpl.ftl](ServiceImpl.ftl)
```diff
- package ${packageName}.${subPackageName};
+ package ${packageName}.service.impl;

- public class ${className} extends BaseServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> implements ${classNamePrefix}Service {}
+ public class ${className}ServiceImpl extends BaseServiceImpl<${classNamePrefix}Mapper, ${classNamePrefix}DO, ${classNamePrefix}Resp, ${classNamePrefix}DetailResp, ${classNamePrefix}Query, ${classNamePrefix}Req> implements ${classNamePrefix}Service {}

```

[AddModal.ftl](..%2Ffrontend%2FAddModal.ftl)
```diff

```

[api.ftl](..%2Ffrontend%2Fapi.ftl)
```diff

```

[DetailDrawer.ftl](..%2Ffrontend%2FDetailDrawer.ftl)
```diff

```

[EditModal.ftl](..%2Ffrontend%2FEditModal.ftl)
```diff

```

[index.ftl](..%2Ffrontend%2Findex.ftl)
```diff

```

[index.ftl](..%2Ffrontend%2Findex.ftl)
```diff

```