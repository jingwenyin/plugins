package com.focus.jingwenyin.mybatis.plugins;

import static org.mybatis.generator.internal.util.StringUtility.isTrue;

import java.util.List;
import java.util.Properties;

import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.TopLevelClass;
/**
 * Created by jingwen on 23/06/2017.
 */
public class ToStringPlugin extends PluginAdapter{
    private boolean useToStringFromRoot;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);
        useToStringFromRoot = isTrue(properties.getProperty("useToStringFromRoot"));
    }

    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelRecordWithBLOBsClassGenerated(
            TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    @Override
    public boolean modelPrimaryKeyClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        generateToString(introspectedTable, topLevelClass);
        return true;
    }

    private void generateToString(IntrospectedTable introspectedTable,
                                  TopLevelClass topLevelClass) {
        Method method = new Method();
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setReturnType(FullyQualifiedJavaType.getStringInstance());
        method.setName("toString"); //$NON-NLS-1$
        if (introspectedTable.isJava5Targeted()) {
            method.addAnnotation("@Override"); //$NON-NLS-1$
        }

        context.getCommentGenerator().addGeneralMethodComment(method,
                introspectedTable);

        method.addBodyLine("StringBuilder sb = new StringBuilder();"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();
        List<Field> fieldList = topLevelClass.getFields();
        for (int i = 0; i < fieldList.size(); i++) {
            String property = fieldList.get(i).getName();
            sb.setLength(0);
            if(property.compareTo("id") == 0) { //"id" represents primary key.
                sb.append("sb.append(\"").append(property) //$NON-NLS-1$ //$NON-NLS-2$
                        .append("=\")").append(".append(").append(property) //$NON-NLS-1$ //$NON-NLS-2$
                        .append(");"); //$NON-NLS-1$
                method.addBodyLine(sb.toString());
                break;
            }
            if (i == fieldList.size() - 1) {
                method.addBodyLine("sb.append(\"Hash = \").append(hashCode());"); //$NON-NLS-1$
            }
        }

        if (useToStringFromRoot && topLevelClass.getSuperClass() != null) {
            method.addBodyLine("sb.append(\", from super class \");"); //$NON-NLS-1$
            method.addBodyLine("sb.append(super.toString());"); //$NON-NLS-1$
        }
        method.addBodyLine("return sb.toString();"); //$NON-NLS-1$

        topLevelClass.addMethod(method);
    }
}