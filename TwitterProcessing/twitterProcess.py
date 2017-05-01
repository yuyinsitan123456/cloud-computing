from nltk import sent_tokenize, word_tokenize
from nltk.corpus import stopwords
import json
import re
import string
import nltk.classify.util
from nltk.classify import NaiveBayesClassifier
from nltk.corpus import movie_reviews


stopwords = stopwords.words('english')

def word_feats(words):
    return dict([(word, True) for word in words])
 
negids = movie_reviews.fileids('neg')
posids = movie_reviews.fileids('pos')
 
negfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'neg') for f in negids]
posfeats = [(word_feats(movie_reviews.words(fileids=[f])), 'pos') for f in posids]
 
trainfeats = negfeats[0:2000] + posfeats[0:2000]
classifier = NaiveBayesClassifier.train(trainfeats)
classifier.show_most_informative_features()
print(classifier.labels())
negtive=0
postive=0

with open('data500.json', 'r') as file:
    for line in file: 
        if not re.match(r'^\s*$', line):
            tweet = json.loads(line)
            text = tweet['text']
            regex = re.compile('[%s]' % re.escape(string.punctuation))
            print(json.dumps(text, indent=4))
            text_no_at = re.sub(r'@\w*','',text)
            text_no_u =re.sub(r'(\\\w{1,5})+','',text_no_at)
            text_no_hash = re.sub(r'#\w*','',text_no_u)
            print(json.dumps(text_no_u,indent=4))
            text_no_hyperlinks=re.sub(r'https?:\/\/.*\/\w*','',text_no_hash)
            text_no_pon = regex.sub('', text_no_hyperlinks)
            tokens = word_tokenize(text_no_pon)
            tokens_lower =[token.lower() for token in tokens]
            print(json.dumps(tokens_lower,indent=4))
            tokens_no_stopwords = [token for token in tokens_lower if token not in stopwords]
            print(json.dumps(tokens_no_stopwords,indent=4))
            outfile = open("processedword.txt","w+")
            #print(tokens_no_stopwords, file = outfile, end ="\n")
            result = classifier.classify(word_feats(tokens_no_stopwords))
            if (result == 'neg'):
                negtive=negtive+1
            else:
                postive= postive+1
            print(result)

print("pos twitter")
print(postive)
print("neg twitter")
print(negtive)
            
