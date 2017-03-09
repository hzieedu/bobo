/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.dudu.bobo.common;

/**
 *
 * @author liangy43
 */
public interface Lifecycle {

    /**
     * 
     */
	void init();

    /**
     * 
     */
    void start();

    /**
     * 
     */
	void stop();

    /**
     * 
     */
	void destroy();
}
