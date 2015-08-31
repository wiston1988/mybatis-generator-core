package org.mybatis.generator.plugins;

import java.util.ArrayList;
import java.util.List;

import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaElement;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.internal.rules.CustomRulesDelegate;
import org.mybatis.generator.internal.util.StringUtility;

/**
 * 生成service类
 * 
 * @author chenhui
 * @date 2014-12-6 上午10:34:45
 */
public class CustomServicePlugin extends PluginAdapter {

	private FullyQualifiedJavaType slf4jLogger;
	private FullyQualifiedJavaType slf4jLoggerFactory;
	private FullyQualifiedJavaType serviceType;
	private FullyQualifiedJavaType daoType;
//	private FullyQualifiedJavaType interfaceType;
	private FullyQualifiedJavaType pojoType;
	private FullyQualifiedJavaType pojoCriteriaType;
	private FullyQualifiedJavaType listType;
	private FullyQualifiedJavaType autowired;
	private FullyQualifiedJavaType service;
	private FullyQualifiedJavaType returnType;
	private String servicePack;
	private String serviceImplPack;
	private String project;
	private String pojoUrl;

	private List<Method> methods;
	//new method name
	private String selectByCriteria;
	private String countByCriteria;
	//old method name
	private String insert ;
	private String insertSelective;
	private String selectByPrimaryKey;
	private String deleteByPrimaryKey;
	private String deleteByExample;
	private String updateByExample;
	private String updateByExampleSelective;
	private String updateByPrimaryKey;
	private String updateByPrimaryKeySelective;
	/**
	 * 是否添加注解
	 */
	private boolean enableAnnotation = true;
	private boolean enableInsert = false;
	private boolean enableInsertSelective = false;
	private boolean enableSelectByPrimaryKey= false;
	private boolean enableDeleteByPrimaryKey = false;
	private boolean enableDeleteByExample = false;
	private boolean enableUpdateByExample = false;
	private boolean enableUpdateByExampleSelective = false;
	private boolean enableUpdateByPrimaryKey = false;
	private boolean enableUpdateByPrimaryKeySelective = false;

	public CustomServicePlugin() {
		super();
		// 默认是slf4j
		slf4jLogger = new FullyQualifiedJavaType("org.slf4j.Logger");
		slf4jLoggerFactory = new FullyQualifiedJavaType("org.slf4j.LoggerFactory");
		methods = new ArrayList<Method>();
	}
	public void initialized(IntrospectedTable introspectedTable) {
		 countByCriteria= "count"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByCriteria";
		 selectByCriteria= "select"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByCriteria";
		 insert = "insert"+introspectedTable.getFullyQualifiedTable().getDomainObjectName();
		 insertSelective = "insert"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"Selective";
		 selectByPrimaryKey = "select"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByPrimaryKey";
		 deleteByPrimaryKey= "delete"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByPrimaryKey";
		 deleteByExample= "delete"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByExample";
		 updateByExample= "update"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByExample";
		 updateByExampleSelective= "update"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByExampleSelective";
		 updateByPrimaryKey= "update"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByPrimaryKey";
		 updateByPrimaryKeySelective= "update"+introspectedTable.getFullyQualifiedTable().getDomainObjectName()+"ByPrimaryKeySelective";
	}
	public boolean validate(List<String> warnings) {
		if (StringUtility.stringHasValue(properties.getProperty("enableAnnotation")))
			enableAnnotation = StringUtility.isTrue(properties.getProperty("enableAnnotation"));

		String enableInsert = properties.getProperty("enableInsert");

		String enableUpdateByExampleSelective = properties.getProperty("enableUpdateByExampleSelective");

		String enableInsertSelective = properties.getProperty("enableInsertSelective");

		String enableUpdateByPrimaryKey = properties.getProperty("enableUpdateByPrimaryKey");

		String enableDeleteByPrimaryKey = properties.getProperty("enableDeleteByPrimaryKey");

		String enableDeleteByExample = properties.getProperty("enableDeleteByExample");

		String enableUpdateByPrimaryKeySelective = properties.getProperty("enableUpdateByPrimaryKeySelective");

		String enableUpdateByExample = properties.getProperty("enableUpdateByExample");

		String enableSelectByPrimaryKey=properties.getProperty("enableSelectByPrimaryKey");
		if (StringUtility.stringHasValue(enableInsert))
			this.enableInsert = StringUtility.isTrue(enableInsert);
		if (StringUtility.stringHasValue(enableSelectByPrimaryKey))
			this.enableSelectByPrimaryKey = StringUtility.isTrue(enableSelectByPrimaryKey);
		if (StringUtility.stringHasValue(enableUpdateByExampleSelective))
			this.enableUpdateByExampleSelective = StringUtility.isTrue(enableUpdateByExampleSelective);
		if (StringUtility.stringHasValue(enableInsertSelective))
			this.enableInsertSelective = StringUtility.isTrue(enableInsertSelective);
		if (StringUtility.stringHasValue(enableUpdateByPrimaryKey))
			this.enableUpdateByPrimaryKey = StringUtility.isTrue(enableUpdateByPrimaryKey);
		if (StringUtility.stringHasValue(enableDeleteByPrimaryKey))
			this.enableDeleteByPrimaryKey = StringUtility.isTrue(enableDeleteByPrimaryKey);
		if (StringUtility.stringHasValue(enableDeleteByExample))
			this.enableDeleteByExample = StringUtility.isTrue(enableDeleteByExample);
		if (StringUtility.stringHasValue(enableUpdateByPrimaryKeySelective))
			this.enableUpdateByPrimaryKeySelective = StringUtility.isTrue(enableUpdateByPrimaryKeySelective);
		if (StringUtility.stringHasValue(enableUpdateByExample))
			this.enableUpdateByExample = StringUtility.isTrue(enableUpdateByExample);

		serviceImplPack = properties.getProperty("targetPackage");
		project = properties.getProperty("targetProject");

		pojoUrl = context.getJavaModelGeneratorConfiguration().getTargetPackage();

		if (enableAnnotation) {
			autowired = new FullyQualifiedJavaType("org.springframework.beans.factory.annotation.Autowired");
			service = new FullyQualifiedJavaType("org.springframework.stereotype.Service");
		}
		return true;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
		List<GeneratedJavaFile> files = new ArrayList<GeneratedJavaFile>();
		String table = introspectedTable.getBaseRecordType();
		String tableName = table.replaceAll(this.pojoUrl + ".", "");

		daoType = new FullyQualifiedJavaType(introspectedTable.getDAOImplementationType());

		// logger.info(toLowerCase(daoType.getShortName()));
		serviceType = new FullyQualifiedJavaType(serviceImplPack + "." + tableName + "Service");

		pojoType = new FullyQualifiedJavaType(pojoUrl + "." + tableName);

		pojoCriteriaType = new FullyQualifiedJavaType(pojoUrl + "." + "Criteria");
		listType = new FullyQualifiedJavaType("java.util.List");
		TopLevelClass topLevelClass = new TopLevelClass(serviceType);
		// 导入必要的类
		addImport( topLevelClass);
		// 实现类
		addServiceImpl(topLevelClass, introspectedTable, tableName, files);
		// 日志
		addLogger(topLevelClass);

		return files;
	}


	/**
	 * 添加实现类
	 * 
	 * @param introspectedTable
	 * @param tableName
	 * @param files
	 */
	protected void addServiceImpl(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String tableName,
			List<GeneratedJavaFile> files) {
		topLevelClass.setVisibility(JavaVisibility.PUBLIC);
		// 设置实现的接口
//		topLevelClass.addSuperInterface(interfaceType);

		if (enableAnnotation) {
			topLevelClass.addAnnotation("@Service");
			topLevelClass.addImportedType(service);
		}
		// 添加引用dao
		addField(topLevelClass, tableName);
		// 添加方法
		topLevelClass.addMethod(countByCriteria(introspectedTable, tableName));
		topLevelClass.addMethod(selectByCriteria(introspectedTable, tableName));


		/**
		 * type 的意义 pojo 1 ;key 2 ;example 3 ;pojo+example 4
		 */
		if (enableSelectByPrimaryKey) {
			topLevelClass.addMethod(selectByPrimaryKey(introspectedTable, tableName));
		}
		if (enableDeleteByPrimaryKey) {
			topLevelClass.addMethod(getOtherInteger(deleteByPrimaryKey, introspectedTable, tableName, 2));
		}
		if (enableUpdateByPrimaryKeySelective) {
			topLevelClass.addMethod(getOtherInteger(updateByPrimaryKeySelective, introspectedTable, tableName, 1));

		}
		if (enableUpdateByPrimaryKey) {
			topLevelClass.addMethod(getOtherInteger(updateByPrimaryKey, introspectedTable, tableName, 1));
		}
		if (enableDeleteByExample) {
			topLevelClass.addMethod(getOtherInteger(deleteByExample, introspectedTable, tableName, 3));
		}
		if (enableUpdateByExampleSelective) {
			topLevelClass.addMethod(getOtherInteger(updateByExampleSelective, introspectedTable, tableName, 4));
		}
		if (enableUpdateByExample) {
			topLevelClass.addMethod(getOtherInteger(updateByExample, introspectedTable, tableName, 4));
		}
		if (enableInsert) {
			topLevelClass.addMethod(getOtherInsertboolean(insert, introspectedTable, tableName));
		}
		if (enableInsertSelective) {
			topLevelClass.addMethod(getOtherInsertboolean(insertSelective, introspectedTable, tableName));
		}
		// 生成文件
		GeneratedJavaFile file = new GeneratedJavaFile(topLevelClass, project,
                context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING),
                context.getJavaFormatter());
		files.add(file);
	}

	/**
	 * 添加字段
	 * 
	 * @param topLevelClass
	 */
	protected void addField(TopLevelClass topLevelClass, String tableName) {
		// 添加 dao
		Field field = new Field();
		field.setName(toLowerCase(daoType.getShortName())); // 设置变量名
		topLevelClass.addImportedType(daoType);
		field.setType(daoType); // 类型
		field.setVisibility(JavaVisibility.PRIVATE);
		if (enableAnnotation) {
			field.addAnnotation("@Autowired");
		}
		topLevelClass.addField(field);
	}

	/**
	 * 添加方法
	 * 
	 */
	protected Method selectByPrimaryKey(IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method();
		method.setName(selectByPrimaryKey);
		method.setReturnType(pojoType);
		if (introspectedTable.getRules().generatePrimaryKeyClass()) {
			FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
			method.addParameter(new Parameter(type, "key"));
		} else {
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
				method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
			}
		}
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		// method.addBodyLine("try {");
		sb.append("return this.");
		sb.append(getDaoShort());
		sb.append(selectByPrimaryKey);
		sb.append("(");
		for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
			sb.append(introspectedColumn.getJavaProperty());
			sb.append(",");
		}
		sb.setLength(sb.length() - 1);
		sb.append(");");
		method.addBodyLine(sb.toString());
		// method.addBodyLine("} catch (Exception e) {");
		// method.addBodyLine("logger.error(\"Exception: \", e);");
		// method.addBodyLine("return null;");
		// method.addBodyLine("}");
		return method;
	}

	/**
	 * 添加方法
	 * 
	 */
	protected Method countByCriteria(IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method();
		method.setName(countByCriteria);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.addParameter(new Parameter(pojoCriteriaType, "criteria"));
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append("int count = this.");
		sb.append(getDaoShort());
		sb.append(countByCriteria);
		sb.append("(");
		sb.append("criteria");
		sb.append(");");
		method.addBodyLine(sb.toString());
		method.addBodyLine("logger.debug(\"count: {}\", count);");
		method.addBodyLine("return count;");
		return method;
	}

	/**
	 * 添加方法
	 * 
	 */
	protected Method selectByCriteria(IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method();
		method.setName(selectByCriteria);
		method.setReturnType(new FullyQualifiedJavaType("List<" + tableName + ">"));
		method.addParameter(new Parameter(pojoCriteriaType, "criteria"));
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		sb.append("return this.");
		sb.append(getDaoShort());
		sb.append(selectByCriteria);
		sb.append("(");
		sb.append("criteria");
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	/**
	 * 添加方法
	 * 
	 */
	protected Method getOtherInteger(String methodName, IntrospectedTable introspectedTable, String tableName, int type) {
		Method method = new Method();
		method.setName(methodName);
		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		String params = addParams(introspectedTable, method, type);
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		// method.addBodyLine("try {");
		sb.append("return this.");
		sb.append(getDaoShort());
		if (introspectedTable.hasBLOBColumns()
				&& (!"updateByPrimaryKeySelective".equals(methodName) && !"deleteByPrimaryKey".equals(methodName)
						&& !"deleteByExample".equals(methodName) && !"updateByExampleSelective".equals(methodName))) {
			sb.append(methodName + "WithoutBLOBs");
		} else {
			sb.append(methodName);
		}
		sb.append('(');
		sb.append(params);
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	/**
	 * 添加方法
	 * 
	 */
	protected Method getOtherInsertboolean(String methodName, IntrospectedTable introspectedTable, String tableName) {
		Method method = new Method();
		method.setName(methodName);
		method.setReturnType(returnType);
		method.addParameter(new Parameter(pojoType, "record"));
		method.setVisibility(JavaVisibility.PUBLIC);
		StringBuilder sb = new StringBuilder();
		if (returnType==null) {
			sb.append("this.");
		} else {
			sb.append("return this.");
		}
		sb.append(getDaoShort());
		sb.append(methodName);
		sb.append("(");
		sb.append("record");
		sb.append(");");
		method.addBodyLine(sb.toString());
		return method;
	}

	/**
	 * type 的意义 pojo 1 key 2 example 3 pojo+example 4
	 */
	protected String addParams(IntrospectedTable introspectedTable, Method method, int type1) {
		switch (type1) {
		case 1:
			method.addParameter(new Parameter(pojoType, "record"));
			return "record";
		case 2:
			if (introspectedTable.getRules().generatePrimaryKeyClass()) {
				FullyQualifiedJavaType type = new FullyQualifiedJavaType(introspectedTable.getPrimaryKeyType());
				method.addParameter(new Parameter(type, "key"));
			} else {
				for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
					FullyQualifiedJavaType type = introspectedColumn.getFullyQualifiedJavaType();
					method.addParameter(new Parameter(type, introspectedColumn.getJavaProperty()));
				}
			}
			StringBuffer sb = new StringBuffer();
			for (IntrospectedColumn introspectedColumn : introspectedTable.getPrimaryKeyColumns()) {
				sb.append(introspectedColumn.getJavaProperty());
				sb.append(",");
			}
			if(sb!=null&&sb.length()>1)
			sb.setLength(sb.length() - 1);
			return sb.toString();
		case 3:
			method.addParameter(new Parameter(pojoCriteriaType, "example"));
			return "example";
		case 4:

			method.addParameter(0, new Parameter(pojoType, "record"));
			method.addParameter(1, new Parameter(pojoCriteriaType, "example"));
			return "record, example";
		default:
			break;
		}
		return null;
	}

	protected void addComment(JavaElement field, String comment) {
		StringBuilder sb = new StringBuilder();
		field.addJavaDocLine("/**");
		sb.append(" * ");
		comment = comment.replaceAll(OutputUtilities.lineSeparator, "<br>"+OutputUtilities.lineSeparator+"\t * ");
		sb.append(comment);
		field.addJavaDocLine(sb.toString());
		field.addJavaDocLine(" */");
	}

	/**
	 * 添加字段
	 * 
	 * @param topLevelClass
	 */
	protected void addField(TopLevelClass topLevelClass) {
		// 添加 success
		Field field = new Field();
		field.setName("success"); // 设置变量名
		field.setType(FullyQualifiedJavaType.getBooleanPrimitiveInstance()); // 类型
		field.setVisibility(JavaVisibility.PRIVATE);
		addComment(field, "执行结果");
		topLevelClass.addField(field);
		// 设置结果
		field = new Field();
		field.setName("message"); // 设置变量名
		field.setType(FullyQualifiedJavaType.getStringInstance()); // 类型
		field.setVisibility(JavaVisibility.PRIVATE);
		addComment(field, "消息结果");
		topLevelClass.addField(field);
	}

	/**
	 * 添加方法
	 * 
	 */
	protected void addMethod(TopLevelClass topLevelClass) {
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setSuccess");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getBooleanPrimitiveInstance(), "success"));
		method.addBodyLine("this.success = success;");
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getBooleanPrimitiveInstance());
		method.setName("isSuccess");
		method.addBodyLine("return success;");
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("setMessage");
		method.addParameter(new Parameter(FullyQualifiedJavaType.getStringInstance(), "message"));
		method.addBodyLine("this.message = message;");
		topLevelClass.addMethod(method);

		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(FullyQualifiedJavaType.getStringInstance());
		method.setName("getMessage");
		method.addBodyLine("return message;");
		topLevelClass.addMethod(method);
	}

	/**
	 * 添加方法
	 * 
	 */
	protected void addMethod(TopLevelClass topLevelClass, String tableName) {
		Method method2 = new Method();
		for (int i = 0; i < methods.size(); i++) {
			Method method = new Method();
			method2 = methods.get(i);
			method = method2;
			method.removeAllBodyLines();
			method.removeAnnotation();
			StringBuilder sb = new StringBuilder();
			sb.append("return this.");
			sb.append(getDaoShort());
			sb.append(method.getName());
			sb.append("(");
			List<Parameter> list = method.getParameters();
			for (int j = 0; j < list.size(); j++) {
				sb.append(list.get(j).getName());
				sb.append(",");
			}
			sb.setLength(sb.length() - 1);
			sb.append(");");
			method.addBodyLine(sb.toString());
			topLevelClass.addMethod(method);
		}
		methods.clear();
	}

	/**
	 * BaseUsers to baseUsers
	 * 
	 * @param tableName
	 * @return
	 */
	protected String toLowerCase(String tableName) {
		StringBuilder sb = new StringBuilder(tableName);
		sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
		return sb.toString();
	}

	/**
	 * BaseUsers to baseUsers
	 * 
	 * @param tableName
	 * @return
	 */
	protected String toUpperCase(String tableName) {
		StringBuilder sb = new StringBuilder(tableName);
		sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
		return sb.toString();
	}

	/**
	 * 导入需要的类
	 */
	private void addImport( TopLevelClass topLevelClass) {
		topLevelClass.addImportedType(daoType);
		topLevelClass.addImportedType(pojoType);
		topLevelClass.addImportedType(pojoCriteriaType);
		topLevelClass.addImportedType(listType);
		topLevelClass.addImportedType(slf4jLogger);
		topLevelClass.addImportedType(slf4jLoggerFactory);
		if (enableAnnotation) {
			topLevelClass.addImportedType(service);
			topLevelClass.addImportedType(autowired);
		}
	}

	/**
	 * 导入logger
	 */
	private void addLogger(TopLevelClass topLevelClass) {
		Field field = new Field();
		field.setFinal(true);
		field.setInitializationString("LoggerFactory.getLogger(" + topLevelClass.getType().getShortName() + ".class)"); // 设置值
		field.setName("logger"); // 设置变量名
		field.setStatic(true);
		field.setType(new FullyQualifiedJavaType("Logger")); // 类型
		field.setVisibility(JavaVisibility.PRIVATE);
		topLevelClass.addField(field);
	}

	private String getDaoShort() {
		return toLowerCase(daoType.getShortName()) + ".";
	}

	public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
		returnType = method.getReturnType();
		return true;
	}
}
