package com.trinetix.app;

/**
 * Описание одного продукта.
 * Created by fess on 5/19/14.
 */
public class Product
{
	public int id;
	public String title;
	public String description;

	public void setImage(String image)
	{
		this.image = image;
	}

	public String image;        //URL for the image
	public String price;
	public String residue;

	public void setId(String id)
	{
		this.id = Integer.valueOf(id);
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public void setPrice(String price)
	{
		this.price = price;
	}

	public void setResidue(String residue)
	{
		this.residue = residue;
	}
}
