

--------

## 1 Git使用
> <br>本文档只是Git的基本操作，进一步深入学习请参考git教程，可在<a href='http://www.runoob.com/git/git-tutorial.html'>here</a>参考学习

###1.1 Git安装配置

- Debian/Ubuntu安装命令

```
$ apt-get install libcurl4-gnutls-dev libexpat1-dev gettext \
  libz-dev libssl-dev
  
$ apt-get install git-core

$ git --version
```

- Centos/RedHat安装命令

```
$ yum install curl-devel expat-devel gettext-devel \
  openssl-devel zlib-devel

$ yum -y install git-core

$ git --version
git version 1.7.1
```

- Windows 平台上安装
<br>Windows平台上可以使用这个工具软件<a href='http://msysgit.github.io/'>msysGit</a>

<img src="http://www.runoob.com/wp-content/uploads/2015/02/20140127131250906" width="300" height="300"></img>

- Mac 平台上安装
<br> 在 Mac 平台上安装 Git 最容易的当属使用图形化的 Git 安装工具<a href='http://sourceforge.net/projects/git-osx-installer/'>here</a>

<img src="http://www.runoob.com/wp-content/uploads/2015/02/18333fig0107-tn.png" width="300" height="300"></img>

###1.2 Git配置

- 配置个人的用户名称和电子邮件地址

```
git config --global user.name "your name"
git config --global user.email yourname@example.com
```
> 备注：
  /etc/gitconfig:系统中所有用户都普遍配置适用的配置，若使用git config 时用--system选项，读写的就是这个文件
  ~/.gitconfig:用户目录下的配置文件只适用与该用户，若适用git config 时用--global选项，读写的就是这个文件
  当前项目下的.git/config：该配置仅仅针对当前项目有效，每一个级别的配置都会覆盖上层的形同配置，所以.git/config里的配置会覆盖/etc/gitconfig中的同名变量
  
- 配置默认使用的文本编辑器

```
git config --global core.editor emacs
```

> Git默认的文本编辑器一般会是Vi或者Vim，可以通过上面语句修改默认的编辑器

- 在解决合并冲突时使用哪种差异化分析工具

```
git config --global merge.tool vimdiff
```

>  Git 可以理解 kdiff3，tkdiff，meld，xxdiff，emerge，vimdiff，gvimdiff，ecmerge，和 opendiff 等合并工具的输出信息。
    当然，你也可以指定使用自己开发的工具,这里不做扩展
   
- 检查已有的配置信息
   
```
git config --list
```

> 有时候会看到重复的变量名，那就说明它们来自不同的配置文件（比如 /etc/gitconfig 和 ~/.gitconfig），不过最终 Git 实际采用的是最后一个。 

这些配置我们也可以在 ~/.gitconfig 或 /etc/gitconfig 看到

```
vim ~/.gitconfig
```

显示内容

```
[http]
    postBuffer = 2M
[user]
    name = runoob
    email = test@runoob.com
```

也可以直接查阅某个环境变量的设定，只要把特定的名字跟在后面即可，像这样：

```
$ git config user.name
runoob
```

- 配置高亮显示

```
git config --global color.status auto #查看状态时高亮显示
git config --global color.branch auto #分之名高亮
git config --global color.ui auto #自动高亮
```

- 命令的别名配置

```
git config --global alias.新名字　原始命令
eg:git config --global alias.co checkout
```

> 示例中展示了对<font color="green">checkout</font>进行重命名为<font color="green">co</font>。其他命令重命名都可进行同样的设置

###1.3 创建仓库

- git init

使用当前目录作为Git仓库,
```
cd 当前目录
git init
```

使用指定目录作为Git仓库

```
git init newrepo
```

> 初始化后，会在 newrepo 目录下会出现一个名为 .git 的目录，所有 Git 需要的数据和资源都存放在这个目录中

如果当前目录下有几个文件想要纳入版本控制，需要先用 git add 命令告诉 Git 开始对这些文件进行跟踪，然后提交
```
git add *.c
git add README
git commit -m '初始化项目版本'
```

> 以上命令将目录下以 .c 结尾及 README 文件提交到仓库中

- git clone

克隆仓库的命令格式为
```
git clone <repo>
eg:git clone git://github.com/schacon/grit.git
```

如果我们需要克隆到指定的目录，可以使用以下命令格式
```
git clone <repo> <directory>
eg:git clone git://github.com/schacon/grit.git mygrit
```

> repo:Git 仓库; directory:本地目录

###1.4 基本操作

- git add将该文件添加到缓存

```
touch README #创建文件
touch hello.php #创建文件
ls #查看
README		hello.php

git status -s # 于查看项目的当前状态

?? README
?? hello.php


```

接下来我们执行 git add 命令来添加文件

```
git add README hello.php 
```

> 使用<font color="green"> git add . </font>命令来添加当前项目的所有文件

- git status 查看在你上次提交之后是否有修改

```
git status # 详细输出内容
git status -s # 简短的结果输出
```

- 执行 git diff 来查看执行 git status 的结果的详细信息

> git diff 命令显示已写入缓存与已修改但尚未写入缓存的改动的区别
  * 尚未缓存的改动：git diff #未进缓存区
  * 查看已缓存的改动： git diff --cached #已经进缓存区
  * 查看已缓存的与未缓存的所有改动：git diff HEAD
  * 显示摘要而非整个 diff：git diff --stat


- git commit

使用 git add 命令将想要快照的内容写入缓存区， 而执行 git commit 将缓存区内容添加到仓库中

```
git commit -m '第一次版本提交' # 使用 -m 选项以在命令行中提供提交注释
```

> 如果你没有设置 -m 选项，Git 会尝试为你打开一个编辑器以填写提交信息。 如果 Git 在你对它的配置中找不到相关信息，默认会打开 vim

如果你觉得 git add 提交缓存的流程太过繁琐，Git 也允许你用 -a 选项跳过这一步。命令格式如下
```
git commit -a
git commit -am '修改 hello.php 文件' # 跳过git add，直接commit并添加comment
```

 - git reset HEAD
 
 git reset HEAD 命令用于取消已缓存的内容
 
 ```
 git reset HEAD -- hello.php 
 ```
> 简而言之，执行 git reset HEAD 以取消之前 git add 添加，但不希望包含在下一提交快照中的缓存

- git rm

git rm 会将条目从缓存区中移除。这与 git reset HEAD 将条目取消缓存是有区别的。 "取消缓存"的意思就是将缓存区恢复为我们做出修改之前的样子。

默认情况下，git rm file 会将文件从缓存区和你的硬盘中（工作目录）删除。

如果你要在工作目录中留着该文件，可以使用 git rm --cached：

- git mv

git mv 命令做得所有事情就是 git rm --cached 命令的操作， 重命名磁盘上的文件，然后再执行 git add 把新文件添加到缓存区。


###1.5 Git 分支管理

- 创建分支命令

```
git branch (branchname)
```

- 切换分支命令

```
git checkout (branchname)
```

- 合并分支命令

```
git merge
```

合并出现冲突后,可通过git status 查询到冲突的文件，解决冲突后，用git add告诉git文件冲突已解决

- 列出分支基本命令

```
git branch
```

- 创建新分支并立即切换

```
git checkout -b (branchname) 
```

- 删除分支命令

```
git branch -d (branchname)
```

###1.6 Git查询提交历史

- git log 命令列出历史提交记录

```
git log
```

- 用 --oneline 选项来查看历史记录的简洁的版本

```
git log --oneline
```

- 用 --graph 选项，查看历史中什么时候出现了分支、合并

```
git log --oneline --graph
```

- 用 '--reverse'参数来逆向显示所有日志

```
git log --reverse --oneline
```

- 查找指定用户的提交日志可以使用命令：git log --author

```
it log --author=Linus --oneline -5 # 查找Linus这个用户，5条记录
```

- 指定日期，可以执行几个选项：--since 和 --before，但是你也可以用 --until 和 --after

```
git log --oneline --before={3.weeks.ago} --after={2010-04-18} --no-merges
```

###1.7 Git标签

如果你达到一个重要的阶段，并希望永远记住那个特别的提交快照，你可以使用 git tag 给它打上标签。
比如说，我们想为我们的 w3cschoolcc 项目发布一个"1.0"版本。 我们可以用 git tag -a v1.0 命令给最新一次提交打上（HEAD）"v1.0"的标签。
-a 选项意为"创建一个带注解的标签"。 不用 -a 选项也可以执行的，但它不会记录这标签是啥时候打的，谁打的，也不会让你添加个标签的注解。 我推荐一直创建带注解的标签。

```
git tag -a v1.0 
```

当我们执行 git log --decorate 时，我们可以看到我们的标签了

```
git log --oneline --decorate --graph
```

如果我们要查看所有标签可以使用以下命令

```
git tag
```

##2 github使用


##3 Git 远程仓库