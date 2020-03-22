file(REMOVE_RECURSE
  "../lib/libxgboost.dylib"
  "../lib/libxgboost.pdb"
)

# Per-language clean rules from dependency scanning.
foreach(lang CXX)
  include(CMakeFiles/xgboost.dir/cmake_clean_${lang}.cmake OPTIONAL)
endforeach()
