package com.example.canteen.bean;

import java.util.List;

public class HomeListBean {

    /**
     * pageNum : 1
     * pageSize : 10
     * size : 2
     * startRow : 1
     * endRow : 2
     * total : 2
     * pages : 1
     * list : [{"id":22,"password":null,"name":"杨妃","companyName":"10000元","age":null,"province":null,"city":null,"departmentName":"总经办","departmentId":5,"tel":"212","industry":"秘书","status":"user","loginIp":"","registeIp":"","pwd":"dba15540c258547a31b8b45b6c7c5bdd","token":null,"email":null},{"id":21,"password":null,"name":"王道王","companyName":"7000元+交通费400元","age":null,"province":null,"city":null,"departmentName":"人事部门","departmentId":1,"tel":"9893","industry":"滴滴司机","status":"user","loginIp":"","registeIp":"","pwd":"5f329d3ac22671f7b214c461e58c27f3","token":null,"email":null}]
     * prePage : 0
     * nextPage : 0
     * isFirstPage : true
     * isLastPage : true
     * hasPreviousPage : false
     * hasNextPage : false
     * navigatePages : 8
     * navigatepageNums : [1]
     * navigateFirstPage : 1
     * navigateLastPage : 1
     * lastPage : 1
     * firstPage : 1
     */

    private String pageNum;
    private String pageSize;
    private String size;
    private String startRow;
    private String endRow;
    private String total;
    private String pages;
    private String prePage;
    private String nextPage;
    private boolean isFirstPage;
    private boolean isLastPage;
    private boolean hasPreviousPage;
    private boolean hasNextPage;
    private String navigatePages;
    private String navigateFirstPage;
    private String navigateLastPage;
    private String lastPage;
    private String firstPage;
    private List<ListBean> list;
    private List<Integer> navigatepageNums;

    public String getPageNum() {
        return pageNum;
    }

    public void setPageNum(String pageNum) {
        this.pageNum = pageNum;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getStartRow() {
        return startRow;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }

    public String getEndRow() {
        return endRow;
    }

    public void setEndRow(String endRow) {
        this.endRow = endRow;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getPrePage() {
        return prePage;
    }

    public void setPrePage(String prePage) {
        this.prePage = prePage;
    }

    public String getNextPage() {
        return nextPage;
    }

    public void setNextPage(String nextPage) {
        this.nextPage = nextPage;
    }

    public boolean isIsFirstPage() {
        return isFirstPage;
    }

    public void setIsFirstPage(boolean isFirstPage) {
        this.isFirstPage = isFirstPage;
    }

    public boolean isIsLastPage() {
        return isLastPage;
    }

    public void setIsLastPage(boolean isLastPage) {
        this.isLastPage = isLastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public String getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(String navigatePages) {
        this.navigatePages = navigatePages;
    }

    public String getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public void setNavigateFirstPage(String navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public String getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateLastPage(String navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

    public String getLastPage() {
        return lastPage;
    }

    public void setLastPage(String lastPage) {
        this.lastPage = lastPage;
    }

    public String getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public List<Integer> getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(List<Integer> navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public static class ListBean {
        /**
         * id : 22
         * password : null
         * name : 杨妃
         * companyName : 10000元
         * age : null
         * province : null
         * city : null
         * departmentName : 总经办
         * departmentId : 5
         * tel : 212
         * industry : 秘书
         * status : user
         * loginIp :
         * registeIp :
         * pwd : dba15540c258547a31b8b45b6c7c5bdd
         * token : null
         * email : null
         */

        private String id;
        private Object password;
        private String name;
        private String companyName;
        private Object age;
        private Object province;
        private Object city;
        private String departmentName;
        private String departmentId;
        private String tel;
        private String industry;
        private String status;
        private String loginIp;
        private String registeIp;
        private String pwd;
        private Object token;
        private Object email;
        private String grade;

        public String getGrade() {
            return grade;
        }

        public void setGrade(String grade) {
            this.grade = grade;
        }
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getPassword() {
            return password;
        }

        public void setPassword(Object password) {
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCompanyName() {
            return companyName;
        }

        public void setCompanyName(String companyName) {
            this.companyName = companyName;
        }

        public Object getAge() {
            return age;
        }

        public void setAge(Object age) {
            this.age = age;
        }

        public Object getProvince() {
            return province;
        }

        public void setProvince(Object province) {
            this.province = province;
        }

        public Object getCity() {
            return city;
        }

        public void setCity(Object city) {
            this.city = city;
        }

        public String getDepartmentName() {
            return departmentName;
        }

        public void setDepartmentName(String departmentName) {
            this.departmentName = departmentName;
        }

        public String getDepartmentId() {
            return departmentId;
        }

        public void setDepartmentId(String departmentId) {
            this.departmentId = departmentId;
        }

        public String getTel() {
            return tel;
        }

        public void setTel(String tel) {
            this.tel = tel;
        }

        public String getIndustry() {
            return industry;
        }

        public void setIndustry(String industry) {
            this.industry = industry;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getLoginIp() {
            return loginIp;
        }

        public void setLoginIp(String loginIp) {
            this.loginIp = loginIp;
        }

        public String getRegisteIp() {
            return registeIp;
        }

        public void setRegisteIp(String registeIp) {
            this.registeIp = registeIp;
        }

        public String getPwd() {
            return pwd;
        }

        public void setPwd(String pwd) {
            this.pwd = pwd;
        }

        public Object getToken() {
            return token;
        }

        public void setToken(Object token) {
            this.token = token;
        }

        public Object getEmail() {
            return email;
        }

        public void setEmail(Object email) {
            this.email = email;
        }
    }
}
