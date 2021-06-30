package org.violetime.autopers.units;

/*
 * 分数
 */
public class FractionalNumber {


    public static void main(String[] args) {
        System.out.println(FractionalNumber.get(1.2d));


    }


    private Long denominator, molecule;

    /**
     * 获得一个分数
     *
     * @param molecule
     * @param denominator
     * @return
     */
    public static FractionalNumber get(Integer molecule, Integer denominator) {
        return new FractionalNumber(molecule, denominator);
    }

    /**
     * 获得一个分数
     *
     * @param value
     * @return
     */
    public static FractionalNumber get(Double value) {
        return new FractionalNumber(value);
    }

    /**
     * 获得一个分数
     *
     * @param value
     * @return
     */
    public static FractionalNumber get(String value) {
        return new FractionalNumber(value);
    }

    /**
     * 构造函数
     *
     * @param molecule    分子
     * @param denominator 分母
     */
    public FractionalNumber(Long molecule, Long denominator) {
        this.molecule = molecule;
        this.denominator = denominator;
        simplification();
    }

    /**
     * 构造函数
     *
     * @param molecule    分子
     * @param denominator 分母
     */
    public FractionalNumber(Integer molecule, Integer denominator) {
        this.molecule = molecule * 1l;
        this.denominator = denominator * 1l;
        simplification();
    }

    /**
     * 构造函数 支持1/2,0.3,1
     *
     * @param value
     */
    public FractionalNumber(String value) {
        if (value.contains("/")) {
            Integer molecule = Integer.parseInt(value.substring(0, value.indexOf("/")));
            Integer denominator = Integer.parseInt(value.substring(value.indexOf("/") + 1));
            this.molecule = molecule * 1l;
            this.denominator = denominator * 1l;
            simplification();

        } else if (value.contains(".")) {

            String val = value.toString();
            boolean flag = true;
            if (val.startsWith("-")) {
                flag = false;
            }

            if (val.contains(".")) {

                String bef = val.substring(0, val.indexOf("."));
                String sub = val.substring(val.indexOf(".") + 1);


                String temp = "1";
                for (int i = 0; i < sub.length(); i++) {
                    temp += "0";
                }
                this.denominator = Long.parseLong(temp);


                this.molecule = Long.parseLong(bef) * this.denominator + Long.parseLong(sub);

            } else {
                this.molecule = Long.parseLong(val);
                this.denominator = 1l;
            }
            if (!flag) {
                this.molecule = -this.molecule;
            }
            simplification();

        } else {
            this.molecule = Long.parseLong(value);
            this.denominator = 1l;
        }
    }

    /**
     * 构造函数
     */
    public FractionalNumber(Double value) {

        if (value == null)
            return;
        String val = value.toString();
        boolean flag = true;
        if (val.startsWith("-")) {
            flag = false;
        }

        if (val.contains(".")) {

            String bef = val.substring(0, val.indexOf("."));
            String sub = val.substring(val.indexOf(".") + 1);


            String temp = "1";
            for (int i = 0; i < sub.length(); i++) {
                temp += "0";
            }
            this.denominator = Long.parseLong(temp);


            this.molecule = Long.parseLong(bef) * this.denominator + Long.parseLong(sub);

        } else {
            this.molecule = Long.parseLong(val);
            this.denominator = 1l;
        }
        if (!flag) {
            this.molecule = -this.molecule;
        }
        simplification();

    }

    /**
     * 最大公约数
     *
     * @param a
     * @param b
     * @return
     */
    private Long greatestCommonDivisor(Long a, Long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        Long max, min;
        max = (a > b) ? a : b;
        min = (a < b) ? a : b;
        if (max % min != 0) {
            return greatestCommonDivisor(min, max % min);
        } else
            return min;
    }

    /**
     * 最小公倍数
     * @param a
     * @param b
     * @return
     */
    private Long minimumCommonMultiple(Long a, Long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        return a * b / greatestCommonDivisor(a, b);
    }

    /**
     * 乘法
     *
     * @param fractionalNumber
     * @return
     */
    public FractionalNumber multiplication(FractionalNumber fractionalNumber) {

        FractionalNumber result = new FractionalNumber(fractionalNumber.molecule * this.molecule, fractionalNumber.denominator * this.denominator);
        return result;
    }

    /**
     * 乘法
     *
     * @param value
     * @return
     */
    public FractionalNumber multiplication(Long value) {

        FractionalNumber result = new FractionalNumber(value * this.molecule, this.denominator);
        return result;
    }

    /**
     * 乘法
     *
     * @param value
     * @return
     */
    public FractionalNumber multiplication(Integer value) {

        FractionalNumber result = new FractionalNumber(value * this.molecule, this.denominator);
        return result;
    }

    /**
     * 乘法
     *
     * @param value
     * @return
     */
    public FractionalNumber multiplication(Double value) {
        FractionalNumber A = new FractionalNumber(value);
        FractionalNumber result = this.multiplication(A);
        return result;
    }

    /**
     * 除法
     *
     * @param fractionalNumber
     * @return
     */
    public FractionalNumber division(FractionalNumber fractionalNumber) {
        FractionalNumber result = new FractionalNumber(this.molecule * fractionalNumber.denominator, this.denominator * fractionalNumber.molecule);
        return result;
    }

    /**
     * 除法
     *
     * @param value
     * @return
     */
    public FractionalNumber division(Long value) {
        FractionalNumber A = new FractionalNumber(value, 1l);

        FractionalNumber result = this.division(A);
        return result;
    }

    /**
     * 除法
     *
     * @param value
     * @return
     */
    public FractionalNumber division(Integer value) {
        FractionalNumber A = new FractionalNumber(value * 1l, 1l);
        FractionalNumber result = this.division(A);
        return result;
    }

    /**
     * 除法
     *
     * @param value
     * @return
     */
    public FractionalNumber division(Double value) {
        FractionalNumber A = new FractionalNumber(value);
        FractionalNumber result = this.division(A);
        return result;
    }

    /**
     * 加法
     *
     * @param fractionalNumber
     * @return
     */
    public FractionalNumber addition(FractionalNumber fractionalNumber) {

        if (this.molecule == 0)
            return fractionalNumber;
        if (fractionalNumber.getMolecule() == 0)
            return this;

        Long denominatorMinimumCommonMultiple = minimumCommonMultiple(this.denominator, fractionalNumber.denominator);
        Long newMoleculeA = this.molecule * denominatorMinimumCommonMultiple / this.denominator;
        Long newMoleculeB = fractionalNumber.molecule * denominatorMinimumCommonMultiple / fractionalNumber.denominator;
        FractionalNumber result = new FractionalNumber(newMoleculeA + newMoleculeB, denominatorMinimumCommonMultiple);
        return result;
    }

    /**
     * 加法
     *
     * @param value
     * @return
     */
    public FractionalNumber addition(Long value) {
        FractionalNumber A = new FractionalNumber(value, 1l);
        FractionalNumber result = this.addition(A);
        return result;
    }

    /**
     * 加法
     *
     * @param value
     * @return
     */
    public FractionalNumber addition(Integer value) {
        FractionalNumber A = new FractionalNumber(value * 1l, 1l);
        FractionalNumber result = this.addition(A);
        return result;
    }

    /**
     * 加法
     *
     * @param value
     * @return
     */
    public FractionalNumber addition(Double value) {
        FractionalNumber A = new FractionalNumber(value);
        FractionalNumber result = this.addition(A);
        return result;
    }

    /**
     * 减法
     *
     * @param fractionalNumber
     * @return
     */
    public FractionalNumber subtraction(FractionalNumber fractionalNumber) {
        if (this.molecule == 0)
            return fractionalNumber.multiplication(-1);
        if (fractionalNumber.getMolecule() == 0)
            return this;

        Long denominatorMinimumCommonMultiple = minimumCommonMultiple(this.denominator, fractionalNumber.denominator);
        Long newMoleculeA = this.molecule * denominatorMinimumCommonMultiple / this.denominator;
        Long newMoleculeB = fractionalNumber.molecule * denominatorMinimumCommonMultiple / fractionalNumber.denominator;
        FractionalNumber result = new FractionalNumber(newMoleculeA - newMoleculeB, denominatorMinimumCommonMultiple);
        return result;
    }

    /**
     * 减法
     *
     * @param value
     * @return
     */
    public FractionalNumber subtraction(Long value) {
        FractionalNumber A = new FractionalNumber(value, 1l);
        FractionalNumber result = this.subtraction(A);
        return result;
    }

    /**
     * 减法
     *
     * @param value
     * @return
     */
    public FractionalNumber subtraction(Integer value) {
        FractionalNumber A = new FractionalNumber(value * 1l, 1l);
        FractionalNumber result = this.subtraction(A);
        return result;
    }

    /**
     * 减法
     *
     * @param value
     * @return
     */
    public FractionalNumber subtraction(Double value) {
        FractionalNumber A = new FractionalNumber(value);
        FractionalNumber result = this.subtraction(A);
        return result;
    }

    /**
     * 求幂
     * @param value
     * @return
     */
    public FractionalNumber power(Double value){
        double mol =Math.pow(this.molecule,value);
        double den =Math.pow(this.denominator,value);
       return   FractionalNumber.get(1d).multiplication(mol).division(den);
    }

    /**
     * 比较大小
     *
     * @param fractionalNumber
     * @return
     */
    public Integer compare(FractionalNumber fractionalNumber) {
        try {
            Long denominatorMinimumCommonMultiple = minimumCommonMultiple(this.denominator, fractionalNumber.denominator);
            Long newMoleculeA = this.molecule * denominatorMinimumCommonMultiple / this.denominator;
            Long newMoleculeB = fractionalNumber.molecule * denominatorMinimumCommonMultiple / fractionalNumber.denominator;
            return newMoleculeA.compareTo(newMoleculeB);
        } catch (Exception e) {
            // TODO: handle exception
        }

        return null;
    }

    /**
     * 是否相等
     *
     * @param fractionalNumber
     * @return
     */
    public boolean equals(FractionalNumber fractionalNumber) {
        try {
            if (this.denominator == fractionalNumber.denominator && this.molecule == fractionalNumber.molecule) {
                return true;
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
        return false;

    }


    /**
     * 分数化简
     */
    private void simplification() {
        if (this.molecule == null || this.denominator == null || this.molecule == 0l || this.denominator == 0l)
            return;
        Long greatestCommonDivisorValue = greatestCommonDivisor(this.molecule, this.denominator);
        if (greatestCommonDivisorValue != 1l) {
            this.molecule = this.molecule / greatestCommonDivisorValue;
            this.denominator = this.denominator / greatestCommonDivisorValue;
        }
    }

    /**
     * 输出double
     *
     * @return
     */
    public Double toDouble() {
        if (denominator == null || molecule == null) {
            return null;
        }
        return molecule * 1.0 / denominator;
    }

    public String toString() {
        if (this.molecule % this.denominator == 0)
            return this.molecule / this.denominator + "";
        return this.molecule + "/" + this.denominator;
    }


    /**
     * 获取分母
     *
     * @return
     */
    public Long getDenominator() {
        return denominator;
    }

    /**
     * 设置分母
     *
     * @param denominator
     */
    public void setDenominator(Long denominator) {
        this.denominator = denominator;
    }

    /**
     * 获取分子
     *
     * @return
     */
    public Long getMolecule() {
        return molecule;
    }

    /**
     * 设置分子
     *
     * @param molecule
     */
    public void setMolecule(Long molecule) {
        this.molecule = molecule;
    }


}
