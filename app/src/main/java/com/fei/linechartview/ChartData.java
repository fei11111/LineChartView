package com.fei.linechartview;

/**
 * @ClassName: ChartData
 * @Description: 描述
 * @Author: Fei
 * @CreateDate: 2021/2/2 11:02
 * @UpdateUser: Fei
 * @UpdateDate: 2021/2/2 11:02
 * @UpdateRemark: 更新说明
 * @Version: 1.0
 */
public class ChartData {

    private String key;
    private String value;

    public ChartData(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
