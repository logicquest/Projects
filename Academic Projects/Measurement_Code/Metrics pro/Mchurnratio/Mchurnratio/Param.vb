Imports System.IO

Public Class Param

    Dim path, path1 As String
    Dim fils(100), basefiles(100), basedir(100), chfiles(20) As String
    Dim i, j, k, bno, nno As Integer
    Dim nwfils(100), newdir(100) As String
    Dim filereg(100), dirreg(100) As String
    Dim totloc, chfno, chuloc, cloc As Integer
    Dim dloc As Integer
    Private Sub Param_Load(sender As Object, e As EventArgs) Handles MyBase.Load
        '   path = "C:\Users\Sekhar\Documents\Downloads\BingDemo"
        lfad(path)

        For d = 0 To i
            Do Until fils(d) = Nothing
                basefiles(d) = fils(d)
                d = d + 1
            Loop
        Next
        bno = i
        For d = 0 To j
            Do Until dirreg(d) = Nothing
                basedir(d) = dirreg(d)
                d = d + 1
            Loop
        Next
        For f = 0 To i - 1
            '      ListBox1.Items.Add(basefiles(f).Remove(0, path.Count + 1))
        Next        '
        '  path1 = "C:\Users\Sekhar\Documents\Downloads\BingDemo1"
        lfad(path1)
        For d = 0 To i
            Do Until fils(d) = Nothing
                nwfils(d) = fils(d)
                d = d + 1
            Loop
        Next
        nno = i
        For d = 0 To j
            Do Until dirreg(d) = Nothing
                newdir(d) = dirreg(d)
                d = d + 1
            Loop
        Next
        For f = 0 To i - 1
            'Replace(nwfils(f), path, "")
            nwfils(f).Remove(path.Count)
            '     ListBox2.Items.Add(nwfils(f))
            'f = f + 1
        Next

        totloc = tloc()
        sep()

        Dim dir As IO.DirectoryInfo
        dir = New IO.DirectoryInfo(path1 + "\vb")
        chfno = chfls(dir)
        relparam()

        display()


    End Sub

    Public Function sep() As Integer()
        Dim newpts(100), count, pos, bpts(100), aloc As Integer

        Dim pobj As New Program
        aloc = 0
        count = 0
        cloc = 0

        Dim str, str1 As String
        For i1 = 0 To nno - 1
            str = nwfils(i1).Remove(0, path1.Count)
            For j1 = 0 To bno - 1
                str1 = basefiles(j1).Remove(0, path.Count)
                If str.CompareTo(str1) = 0 Then
                    newpts(count) = i1
                    bpts(count) = j1
                    '  cloc = cloc + pobj.reading(basefiles(bpts(count)), nwfils(newpts(count)))
                    count = count + 1
                End If
            Next
        Next
        Dim bbloc, nvloc, dloc2 As Integer
        dloc2 = 0
        For g = 0 To count - 1
            bbloc = loc(basefiles(bpts(g)))
            nvloc = loc(nwfils(newpts(g)))
            If nvloc < bbloc Then
                dloc2 = dloc2 + (bbloc - nvloc)
            ElseIf nvloc > bbloc Then
                cloc = nvloc - bbloc
            End If
        Next
        dloc = dloc2
        Dim flag, cpts(100), count1 As Integer
        count1 = 0
        For m = 0 To nno - 1
            flag = 0
            For n1 = 0 To count - 1
                If m = newpts(n1) Then
                    flag = 1
                End If
            Next
            If flag = 0 Then
                cpts(count1) = m
                count1 = count1 + 1
            End If
        Next
        For m = 0 To count1 - 1
            aloc = aloc + loc(nwfils(cpts(m)))
        Next
        chuloc = cloc + aloc
        Return newpts
    End Function


    Public Function chfls(dir As IO.DirectoryInfo) As Integer
        Dim count As Integer
        count = 0
        For Each di As IO.FileInfo In dir.GetFiles
            If IO.Path.GetExtension(di.Name) = ".vb" Then
                chfiles(count) = di.FullName
                count = count + 1
            End If
        Next
        Return count
    End Function
    Public Function lfad(path As String)
        i = 0
        j = 1
        k = 0
        Dim dir As IO.DirectoryInfo
        dirreg(k) = path
        Do While k < j
            dir = New IO.DirectoryInfo(dirreg(k))
            savefilepath(dir)
            getdir(dir)
            k = k + 1
        Loop
        Return Nothing
    End Function
    Public Function savefilepath(dir As IO.DirectoryInfo) As String()
        For Each di As IO.FileInfo In dir.GetFiles
            If IO.Path.GetExtension(di.Name) = ".vb" Then
                fils(i) = di.FullName
                i = i + 1
            End If
        Next
        Return fils
    End Function
    Public Function getdir(dir As IO.DirectoryInfo) As String()

        For Each dr As IO.DirectoryInfo In dir.GetDirectories
            dirreg(j) = dr.FullName
            j = j + 1
        Next
        Return dirreg
    End Function
    Public Function loc(fpath As String) As Integer
        Dim strLine As String
        Dim floc As Integer
        floc = 0
        strLine = "c"
        Dim aFile As New FileStream(fpath, FileMode.Open, FileAccess.Read)
        Dim sr As New StreamReader(aFile)
        Do While sr.Peek <> -1
            strLine = sr.ReadLine()
            floc = floc + 1
        Loop
        floc = floc - 1
        aFile.Close()
        Return floc
    End Function
    Public Function tloc() As Integer
        Dim tloc1 As Integer
        tloc1 = 0
        For d = 0 To nno - 1
            tloc1 = tloc1 + loc(nwfils(d))
        Next
        Return tloc1
    End Function
    Sub pass(str As String, str1 As String)
        path = str
        path1 = str1
    End Sub

    Public Function display()
        Label1.Text = Label1.Text + "      " + nno.ToString
        Label2.Text = Label2.Text + "      " + chuloc.ToString
        Label3.Text = Label3.Text + "      " + totloc.ToString
        Label4.Text = Label4.Text + "      " + dloc.ToString
        Label6.Text = Label6.Text + "      " + cloc.ToString
        Label5.Text = Label5.Text + "      " + chfno.ToString
        Return Nothing
    End Function

    Public Function relparam()

        Dim m1, m2, m3, m4, m5, m6 As Double
        Try
            m1 = chuloc / totloc
            m2 = dloc / totloc
            m3 = chfno / nno
            m4 = cloc / chfno
            m5 = chuloc / dloc
            Label9.Text = Label9.Text + "      " + m1.ToString
            Label10.Text = Label10.Text + "      " + m2.ToString
            Label11.Text = Label11.Text + "      " + m3.ToString
            Label12.Text = Label12.Text + "      " + m4.ToString
            Label13.Text = Label13.Text + "      " + m5.ToString
        Catch ex As Exception

        End Try

        Return Nothing
    End Function



End Class