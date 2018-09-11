# TraceGenerator

## Description
MTSA出力の環境モデルからトレースを自動生成するツールです。 <br>
各遷移の遷移確率を設定できます。<br>
遷移確率を設定しなかった場合、その遷移を選択する確率は``1/(状態qから出ている遷移数)``になります。 <br>
実験用に作成しました。 <br>

## Set Up
githubよりクローンしてください。
```
$ cd workspace
$ git clone https://github.com/iidachihiro/TraceGenerator.git
```
``resources``ディレクトリと``output``ディレクトリを作成してください。 <br>
```
$ cd TraceGenerator
$ mkdir resources
$ mkdir output
```
``resources``ディレクトリに必要なものは以下の2つ(3つ)です。 <br>
* resources
  * MTSAの出力する環境モデル(.txt)
  * (オプション)遷移確率(.csv)
  ```
  PreState,Action,PostState,Probability
  ```
  * 設定ファイル(generator.config)
  ```
  Transition File Name =
	     sample_envModel.txt
  Probability File Name =
	     sample_ProbabilityConfiguration.csv
  Trace Size =
	     10000
  Probability Setting =
	     true
  ```
    * Transition File Name: MTSAの出力する環境モデルを記述したファイル
    * Probability File Name: 設定したい遷移確率を列挙したファイル、全ての遷移について書く必要はない
    * Trace Size: 生成したいトレースのサイズ、実際に生成されるトレースのサイズは、初期状態も含めるので+1される。
    * Probability Setting: 遷移確率を自分で設定したい(Probability Fileを適用したい)場合はtrueにする。

``output``ディレクトリには生成された``Traces.txt``が出力されます。

## Run
まずコンパイルして下さい。
```
$ mkdir bin
$ javac src/* -d bin
$ cd TraceGenerator/bin
```

### Prepare
上記の遷移確率表のファイルを``resources``に自動生成します。 <br>
ファイル名は``generator.config``の``Probability File Name``に設定した名前になります。
```
$ java Main prepare
```

### Generate
トレースを生成します。
```
$ java Main generate
```
