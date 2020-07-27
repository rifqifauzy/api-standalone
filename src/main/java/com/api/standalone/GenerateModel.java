package com.api.standalone;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.lang.model.element.Modifier;

import org.javalite.activejdbc.Base;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

public class GenerateModel {

	/*set model package*/
	private static final String PACKAGE = "com.api.standalone.model";
	
	/*execute generate*/
	public static void main(String[] args) throws SQLException, IOException {
		generate("persons");
	}
	
	static void generate(String tableName) throws SQLException, IOException {
		/*open connection*/ 
		Base.open();
		String lowerTableName = tableName.toLowerCase();
		Statement stmt = Base.connection().createStatement();
		
		/*select data*/
        ResultSet rs = stmt.executeQuery("select * from ".concat(tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        List<MethodSpec> methodSpecs = new LinkedList<>();
        for (int i=1; i<=rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            
            /*Skip jika column ID karena di javalite sudah ada getId, bisa tidak diskip jika ID di database bukan auto increment*/
            if (columnName.equalsIgnoreCase("id"))
            	continue;
            
            /*get Class berdasarkan tipe column*/
            Class<?> columnClass = getColumnClass(rsmd.getColumnType(i));
            
            /*menentukan type method berdasarkan tipe column*/
            String typedMethodName = getColumnTypeMethodName(rsmd.getColumnType(i));

            /*create getter method*/
            MethodSpec getMethod = MethodSpec.methodBuilder("get" + toTitleCase(columnName.toLowerCase()))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(columnClass)
                    .addStatement("return get$L($S)", typedMethodName, columnName)
                    .build();
            methodSpecs.add(getMethod);

            String parameterName = toParamCase(columnName.toLowerCase());
            
            /*create setter method*/
            MethodSpec setMethod = MethodSpec.methodBuilder("set" + toTitleCase(columnName.toLowerCase()))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(columnClass, parameterName)
                    .returns(void.class)
                    .addStatement("set$L($S, $L)", typedMethodName, columnName, parameterName)
                    .build();
            methodSpecs.add(setMethod);
        }
        
        /*create Annotation berdasarkan nama table dan ID name saya hardcode dengan nama column "id" */
        TypeSpec typeSpec = TypeSpec.classBuilder(toTitleCase(lowerTableName))
					.addAnnotation(AnnotationSpec.builder(Table.class).addMember("value", "$S", tableName).build())
					.addAnnotation(AnnotationSpec.builder(IdName.class).addMember("value", "$S", "id").build())
					.superclass(Model.class).addModifiers(Modifier.PUBLIC).addMethods(methodSpecs).build();
		writeJavaFile(lowerTableName, typeSpec);

		/*close connection*/
		Base.close();
	}

	/*write to Java file*/
	private static void writeJavaFile(String lowerTableName, TypeSpec typeSpec)
			throws FileNotFoundException, UnsupportedEncodingException, IOException {
		JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec).build();

		PrintStream stream = new PrintStream(fileName(lowerTableName), "UTF-8");
		javaFile.writeTo(stream);

		System.out.println(toTitleCase(lowerTableName).concat(" generated"));
	}
	
	private static String fileName(String tableName) {
		String path = PACKAGE.replace(".", "/");				
		return "src/main/java/".concat(path).concat("/").concat(toTitleCase(tableName)).concat(".java");
	}
	
	private static Class<?> getColumnClass(int columnType) {
		switch (columnType) {
		case Types.VARCHAR:
		case Types.NCHAR:
			return String.class;
		case Types.DATE:
			return Date.class;
		case Types.TIME:
		case Types.TIME_WITH_TIMEZONE:
			return Time.class;
		case Types.TIMESTAMP:
		case Types.TIMESTAMP_WITH_TIMEZONE:
			return Timestamp.class;
		case Types.INTEGER:
			return Integer.class;
		case Types.DECIMAL:
		case Types.DOUBLE:
			return Double.class;
		case Types.NUMERIC:
			return BigDecimal.class;
		default:
			return Object.class;
		}
	}

	private static String getColumnTypeMethodName(int columnType) {
		switch (columnType) {
		case Types.VARCHAR:
		case Types.NCHAR:
			return "String";
		case Types.DATE:
			return "Date";
		case Types.TIME:
		case Types.TIME_WITH_TIMEZONE:
			return "Time";
		case Types.TIMESTAMP:
		case Types.TIMESTAMP_WITH_TIMEZONE:
			return "Timestamp";
		case Types.INTEGER:
			return "Integer";
		case Types.DECIMAL:
		case Types.DOUBLE:
			return "Double";
		case Types.NUMERIC:
			return "BigDecimal";
		default:
			return "";
		}
	}

	private static String toTitleCase(String s) {
		return capitalize(toCamelCase(s));
	}

	private static String toCamelCase(String s) {
		String[] parts = s.split("_");
		StringBuilder camelCaseString = new StringBuilder();
		for (String part : parts) {
			camelCaseString.append(capitalize(part));
		}
		return capitalize(camelCaseString.toString());
	}

	private static String toParamCase(String s) {
		String[] parts = s.split("_");
		StringBuilder paramString = new StringBuilder();
		for (int i = 0; i < parts.length; i++) {
			paramString.append(i==0?parts[i]:capitalize(parts[i]));
		}
		return paramString.toString();
	}
	
	private static String capitalize(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

}
