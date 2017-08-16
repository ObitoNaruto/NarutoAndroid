/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.naruto.mobile.framework.service.common.multimediabiz.biz.src.tv.danmaku.ijk.media.gles;

import java.nio.FloatBuffer;

public class Drawable2d {
    public static final int SIZEOF_FLOAT = 4;

    private static final float FULL_RECTANGLE_COORDS[] = {
        -1.0f, -1.0f,   // 0 bottom left
         1.0f, -1.0f,   // 1 bottom right
        -1.0f,  1.0f,   // 2 top left
         1.0f,  1.0f,   // 3 top right
    };
    private static final float FULL_RECTANGLE_TEX_COORDS[] = {
        0.0f, 0.0f,     // 0 bottom left
        1.0f, 0.0f,     // 1 bottom right
        0.0f, 1.0f,     // 2 top left
        1.0f, 1.0f      // 3 top right
    };
//    private static final float FULL_RECTANGLE_TEX_COORDS[] = {
//        0.0f, 280/1280.0f,     // 0 bottom left
//        1.0f, 280/1280.0f,     // 1 bottom right
//        0.0f, 1000/1280.0f,     // 2 top left
//        1.0f, 1000/1280.0f      // 3 top right
//    };
    private static final FloatBuffer FULL_RECTANGLE_BUF =
            GlUtil.createFloatBuffer(FULL_RECTANGLE_COORDS);
    private static final FloatBuffer FULL_RECTANGLE_TEX_BUF =
            GlUtil.createFloatBuffer(FULL_RECTANGLE_TEX_COORDS);


    private FloatBuffer mVertexArray;
    private FloatBuffer mTexCoordArray;
    private int mVertexCount;
    private int mCoordsPerVertex;
    private int mVertexStride;
    private int mTexCoordStride;

    public Drawable2d() {
        mVertexArray = FULL_RECTANGLE_BUF;
        mTexCoordArray = FULL_RECTANGLE_TEX_BUF;
        mCoordsPerVertex = 2;
        mVertexStride = mCoordsPerVertex * SIZEOF_FLOAT;
        mVertexCount = FULL_RECTANGLE_COORDS.length / mCoordsPerVertex;
        mTexCoordStride = 2 * SIZEOF_FLOAT;
    }

    public FloatBuffer getVertexArray() {
        return mVertexArray;
    }


    public FloatBuffer getTexCoordArray() {
        return mTexCoordArray;
    }

    public int getVertexCount() {
        return mVertexCount;
    }

    public int getVertexStride() {
        return mVertexStride;
    }

    /**
     * Returns the width, in bytes, of the data for each texture coordinate.
     */
    public int getTexCoordStride() {
        return mTexCoordStride;
    }

    /**
     * Returns the number of position coordinates per vertex.  This will be 2 or 3.
     */
    public int getCoordsPerVertex() {
        return mCoordsPerVertex;
    }
}
