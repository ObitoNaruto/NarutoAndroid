// ISecurityCenter.aidl
package com.naruto.mobile.base.binderPool;

// Declare any non-default types here with import statements

interface ISecurityCenter {
   String encrypt(String content);
   String decrypt(String password);
}
