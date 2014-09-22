package model;

/**
 *
 * @author hsty
 */
public class Person
{
    int id;
    String name;
    int age;

    public Person(int id, String name, int age)
    {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public int getId()
    {
        return id;
    }

    public int getAge()
    {
        return age;
    }

    public String getName()
    {
        return name;
    }

    public void setId(int id)
    {
        this.id = id;
    }
    
    
}
