package org.secfirst.umbrella.models.Relief.Countries;

import java.util.ArrayList;

public class RWCountries
{
    private String time;

    private String count;

    private String totalCount;

    private ArrayList<Data> data;

    private Links links;

    private String href;

    public String getTime ()
    {
        return time;
    }

    public void setTime (String time)
    {
        this.time = time;
    }

    public String getCount ()
    {
        return count;
    }

    public void setCount (String count)
    {
        this.count = count;
    }

    public String getTotalCount ()
    {
        return totalCount;
    }

    public void setTotalCount (String totalCount)
    {
        this.totalCount = totalCount;
    }

    public ArrayList<Data> getData()
    {
        return data;
    }

    public void setData (ArrayList<Data> data)
    {
        this.data = data;
    }

    public Links getLinks ()
    {
        return links;
    }

    public void setLinks (Links links)
    {
        this.links = links;
    }

    public String getHref ()
    {
        return href;
    }

    public void setHref (String href)
    {
        this.href = href;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [time = "+time+", count = "+count+", totalCount = "+totalCount+", data = "+data+", links = "+links+", href = "+href+"]";
    }
}