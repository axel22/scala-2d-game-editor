


-- dumps the mesh point into a file
function exportMesh obj filename = (
	local file = createFile filename
	
	format "Vertices\n" to:file
	for i in 1 to obj.numVerts do
	(
		local v = coordsys obj getVert obj i
		format "%, %, %\n" (0 as integer) (i - 1) v to:file
	)
	format "Faces\n" to:file
	for i in 1 to obj.numFaces do
	(
		local f = coordsys obj getFace obj i
		format "%, %, %\n" (0 as integer) (i - 1) f to:file
	)
	
	close file
	edit filename
)


rollout ExportMeshTool "Export mesh" (
	edittext filenameText "Filename: " style_sunkenedge:true
	button selectFile "Select file"
	button exportToFile "Export mesh to file"
	
	on selectFile pressed do
		filenameText.text = getSaveFileName caption:"Choose file name for mesh"
	
	on exportToFile pressed do
		if (filenameText.text != "") then (
			exportMesh $ filenameText.text
		) else (
			messageBox "Please specify a file." title:"No file selected."
		)
)

createDialog ExportMeshTool width:250
