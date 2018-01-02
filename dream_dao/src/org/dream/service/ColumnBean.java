package org.dream.service;

public class ColumnBean
{
  private String columnName;
  private String type;
  private String description;
  private boolean primary = false;
  private boolean autoId = false;
  private boolean allowNull = true;

  public String getColumnName() {
    return this.columnName;
  }
  public void setColumnName(String columnName) {
    this.columnName = columnName;
  }
  public String getType() {
    return this.type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public boolean isAutoId() {
    return this.autoId;
  }
  public void setAutoId(boolean autoId) {
    this.autoId = autoId;
  }
  public boolean isPrimary() {
    return this.primary;
  }
  public void setPrimary(boolean primary) {
    this.primary = primary;
  }
  public boolean isAllowNull() {
    return this.allowNull;
  }
  public void setAllowNull(boolean allowNull) {
    this.allowNull = allowNull;
  }
  public String getDescription() {
    return this.description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}