from konlpy.tag import Kkma
from konlpy.tag import Twitter
from nltk.corpus import brown
from nltk.cluster.util import cosine_distance
import numpy as np
from operator import itemgetter

def pagerank(A, eps=0.0001, d=0.85):
    P = np.ones(len(A))/len(A)
    while True:
        new_P = np.ones(len(A)) * (1-d) / len(A) + d*A.T.dot(P)
        delta = abs((new_P - P).sum())
        if delta <= eps:
            return new_P
        P = new_P

def sentence_similarity(sent1, sent2, stopwords=None):
    if stopwords is None:
        stopwords = []

    all_words = list(set(sent1+sent2))

    vector1 = [0] * len(all_words)
    vector2 = [0] * len(all_words)


    for w in sent1:
        if w in stopwords:
            continue

        vector1[all_words.index(w)] += 1

    for w in sent2:
        if w in stopwords:
            continue
        vector2[all_words.index(w)] += 1

    return 1 - cosine_distance(vector1, vector2)

def build_similaryity_matrix(sentences, stopwords=None):
    S = np.zeros((len(sentences), len(sentences)))

    for idx1 in range(len(sentences)):
        for idx2 in range(len(sentences)):
            if idx1 == idx2:
                continue
            S[idx1][idx2] = sentence_similarity(sentences[idx1], sentences[idx2], stopwords)

    for idx in range(len(S)):
        S[idx] /= S[idx].sum()

    return S

def textrank(sentences, top_n=5, stopwords=None):
    S = build_similaryity_matrix(sentences, stopwords)
    sentence_ranks = pagerank(S)

    ranked_sentence_indexes = [item[0] for item in sorted(enumerate(sentence_ranks), key=lambda item: -item[1])]
    selected_sentences = sorted(ranked_sentence_indexes[:top_n])
    print(selected_sentences)
    summary = itemgetter(*selected_sentences)(sentences_last)
    return summary

kkma = Kkma()
content ='  그러면 먼저 대규모유통업에서의 거래 공정화에 관한 법률 일부개정법률안(대안)을 의결하도록 하겠습니다.' \
'  투표해 주시기 바랍니다.' \
'  투표를 다 하셨습니까? ' \
'  투표를 마치겠습니다. ' \
'  투표 결과를 말씀드리겠습니다. ' \
'  재석 214인 중 찬성 211인, 기권 3인으로서 대규모유통업에서의 거래 공정화에 관한 법률 일부개정법률안(대안)은 가결되었음을 선포합니다. ' \
'  다음은 소비자기본법 일부개정법률안(대안)을 의결하도록 하겠습니다.' \
'  투표해 주시기 바랍니다.' \
'  투표를 다 하셨습니까? ' \
'  투표를 마치겠습니다. ' \
'  투표 결과를 말씀드리겠습니다. ' \
'  재석 216인 중 찬성 213인, 기권 3인으로서 소비자기본법 일부개정법률안(대안)은 가결되었음을 선포합니다. ' \
'  다음은 전기통신금융사기 피해 방지 및 피해금 환급에 관한 특별법 일부개정법률안을 의결하도록 하겠습니다. ' \
'  투표해 주시기 바랍니다. ' \
'  투표를 다 하셨습니까? ' \
'  투표를 마치겠습니다. ' \
'  투표 결과를 말씀드리겠습니다. ' \
'  재석 214인 중 찬성 211인, 기권 3인으로서 전기통신금융사기 피해 방지 및 피해금 환급에 관한 특별법 일부개정법률안은 정무위원회의 수정안대로 가결되었음을 선포합니다. ' \
'  다음은 이중상환청구권부 채권 발행에 관한 법률 일부개정법률안을 의결하도록 하겠습니다. ' \
'  투표해 주시기 바랍니다.' \
'  투표를 다 하셨습니까? ' \
'  투표를 마치겠습니다. ' \
'  투표 결과를 말씀드리겠습니다. ' \
'  재석 211인 중 찬성 207인, 기권 4인으로서 이중상환청구권부 채권 발행에 관한 법률 일부개정법률안은 가결되었음을 선포합니다. '


stopwords = []

twitter = Twitter()
sentences_last = kkma.sentences(content)
print(sentences_last)
new_sentences = []
point = ['Noun', 'Verb', 'Number', 'Adjective']

noun = 0
verb = 0
number = 0
adj = 0
for s in sentences_last:
    sent_pos = twitter.pos(s, True, True)
    print(twitter.pos(s,True,True))
    sent_list = []
    for w in sent_pos:
        if(w[1] in point):
            sent_list.append(w[0])

        if(w[1] == 'Noun'):
            noun += 1
        elif (w[1] == 'Verb'):
            verb += 1
        elif (w[1] == 'Number'):
            number += 1
        elif (w[1] == 'Adjective'):
            adj += 1

    new_sentences.append(sent_list)

print(new_sentences)

for idx, sentence in enumerate(textrank(new_sentences)):
    print("%s. %s"%((idx+1), ' '.join(sentence)))

print(noun,' ', verb, ' ', number, ' ', adj)