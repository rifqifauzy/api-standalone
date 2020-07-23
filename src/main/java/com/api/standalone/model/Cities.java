package com.api.standalone.model;

import java.lang.Integer;
import java.lang.String;
import org.javalite.activejdbc.Model;
import org.javalite.activejdbc.annotations.IdName;
import org.javalite.activejdbc.annotations.Table;

@Table("cities")
@IdName("id")
public class Cities extends Model {
  public String getName() {
    return getString("name");
  }

  public void setName(String Name) {
    setString("name", Name);
  }

  public Integer getProvinceId() {
    return getInteger("province_id");
  }

  public void setProvinceId(Integer ProvinceId) {
    setInteger("province_id", ProvinceId);
  }
}
