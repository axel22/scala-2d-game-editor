EXT=$1
HEADER=$2
PATTERN=$3

if ($# -ne 3)
then
  echo Usage: add-headers <extension> <header-file> <not-present-pattern>
  exit 1
fi

FILES=`find *.$1 ../src/main`

echo Modifying:
echo $FILES

for i in $FILES
do
  if ! grep -q $PATTERN $i
  then
    cat $HEADER $i >$i.new && mv $i.new $i
  fi
done
