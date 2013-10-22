/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.solr.analysis;

import java.io.Reader;
import java.util.Map;

import net.paoding.analysis.analyzer.PaodingTokenizer;
import net.paoding.analysis.analyzer.TokenCollector;
import net.paoding.analysis.analyzer.impl.MaxWordLengthTokenCollector;
import net.paoding.analysis.analyzer.impl.MostWordsTokenCollector;
import net.paoding.analysis.knife.PaodingMaker;

import org.apache.lucene.analysis.Tokenizer;

/**
 * Factory for {@link ChineseTokenizer}
 */
public class ChineseTokenizerFactory extends BaseTokenizerFactory {
	/** 最多切分 默认模式 */
	public static final String MOST_WORDS_MODE = "most-words";
	/** 按最大切分 */
	public static final String MAX_WORD_LENGTH_MODE = "max-word-length";
	private String mode = null;

	public void setMode(String mode) {
		if (mode == null || MOST_WORDS_MODE.equalsIgnoreCase(mode)
				|| "default".equalsIgnoreCase(mode)) {
			this.mode = MOST_WORDS_MODE;
		} else if (MAX_WORD_LENGTH_MODE.equalsIgnoreCase(mode)) {
			this.mode = MAX_WORD_LENGTH_MODE;
		} else {
			throw new IllegalArgumentException("不合法的分析器Mode参数设置:" + mode);
		}
	}

	@Override
	public void init(Map<String, String> args) {
		super.init(args);
		setMode((String) args.get("mode"));
	}

	public Tokenizer create(Reader input) {
		return new PaodingTokenizer(input, PaodingMaker.make(),
		createTokenCollector());
	}

	private TokenCollector createTokenCollector() {
		if (MOST_WORDS_MODE.equals(mode))
			return new MostWordsTokenCollector();
		if (MAX_WORD_LENGTH_MODE.equals(mode))
			return new MaxWordLengthTokenCollector();
		throw new Error("never happened");
	}
}
