package Multi;

/*
    代码中体现多态性，其实就是：父类引用指向子类对象

    格式：
        父类名称 对象名 = new 子类名称();
        或者:
        接口名称 对象名 = new 实现类名称();
 */
/*
    访问成员变量的两种方式
    1.直接通过对象名称访问成员变量：看等号左边是谁，优先用谁，没有则向上找
    2.间接通过成员方法访问成员变量：看该方法属于谁，优先用谁，没有则向上找
 */
/*
    在多态的代码中，成员方法的访问规则是：
        看new的是谁，就优先用谁，没有则向上找

    口诀：编译看左边，运行看右边

    对比：
        成员变量:编译看左边，运行还看左边
        成员方法：编译看左边，运行看右边
 */
/*
    1.对象的向上转型，其实就是多态写法
    格式：父类名称 对象名 = new 子类名称();
    含义：右侧创建一个子类对象，把它当作父类来看待使用
    注意事项：向上转型一定是安全的，从小范围转向了大范围。
    弊端：对象一旦向上转型为父类，就无法调用子类原本特有的内容

    2.对象的向下转型，其实就是一个【还原】动作
    格式：子类名称 对象名 = (子类名称) 父类名称；
    含义：将父类对象，【还原】成为本来的子类对象
    注意事项：
        a.必须保证对象本来创建的时候就是子类对象，才能向下转型。
        b.必须保证子类对象相同
 */
public class MultiDemo {

    public static void main(String[] args) {
        //使用多态的写法
        //左侧父类的引用，指向了右侧子类的对象
        Dad obj = new Son();

        System.out.println(obj.num); //10

//        System.out.println(obj.numSon);//错误写法！！
        obj.method();//子类方法
        obj.methodDad();//父类特有方法


    }
}
