#!/usr/bin/env bash
set -e

# copied from https://github.com/VirtusLab/scala-cli/blob/b73b3e612eeba09c3231da9a51720cb8ddff1874/project/musl-image/setup.sh

cd /usr/local/musl/bin

for i in x86_64-unknown-linux-musl-*; do
  dest="$(echo "$i" | sed 's/-unknown//')"
  ln -s "$i" "$dest"
done
