package com.api.standalone;

import java.io.IOException;
import java.io.PrintStream;
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

	private static final String PACKAGE = "com.api.standalone.model";
	
	public static void main(String[] args) throws SQLException, IOException {
		generate("cities");
	}
	
	static void generate(String tableName) throws SQLException, IOException {
		Base.open();
		Statement stmt = Base.connection().createStatement();
        ResultSet rs = stmt.executeQuery("select * from ".concat(tableName));
        ResultSetMetaData rsmd = rs.getMetaData();
        List<MethodSpec> methodSpecs = new LinkedList<>();
        for (int i=1; i<=rsmd.getColumnCount(); i++) {
            String columnName = rsmd.getColumnName(i);
            if (columnName.equalsIgnoreCase("id"))
            	continue;
            Class<?> columnClass = getColumnClass(rsmd.getColumnType(i));
            String typedMethodName = getColumnTypeMethodName(rsmd.getColumnType(i));

            MethodSpec getMethod = MethodSpec.methodBuilder("get" + toTitleCase(columnName))
                    .addModifiers(Modifier.PUBLIC)
                    .returns(columnClass)
                    .addStatement("return get$L($S)", typedMethodName, columnName)
                    .build();
            methodSpecs.add(getMethod);

            String parameterName = toCamelCase(columnName);
            MethodSpec setMethod = MethodSpec.methodBuilder("set" + toTitleCase(columnName))
                    .addModifiers(Modifier.PUBLIC)
                    .addParameter(columnClass, parameterName)
                    .returns(void.class)
                    .addStatement("set$L($S, $L)", typedMethodName, columnName, parameterName)
                    .build();
            methodSpecs.add(setMethod);
        }
        
        TypeSpec typeSpec = TypeSpec.classBuilder(toTitleCase(tableName))
        		.addAnnotation(AnnotationSpec.builder(Table.class).addMember("value", "$S", tableName).build())
        		.addAnnotation(AnnotationSpec.builder(IdName.class).addMember("value", "$S", "id").build())
                .superclass(Model.class)
                .addModifiers(Modifier.PUBLIC)
                .addMethods(methodSpecs)
                .build();

        JavaFile javaFile = JavaFile.builder(PACKAGE, typeSpec)
                .build();

        PrintStream stream = new PrintStream(fileName(tableName), "UTF-8");
        javaFile.writeTo(stream);
        
        System.out.println(toTitleCase(tableName).concat(" generated"));
        
		Base.close();
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

	private static String capitalize(String original) {
		if (original == null || original.length() == 0) {
			return original;
		}
		return original.substring(0, 1).toUpperCase() + original.substring(1);
	}

}
