import React from 'react';
import { AcademicTranscriptReport } from '../../types/transcript';
import { GraduationCap, Printer, X, ShieldCheck, Award, CheckCircle2 } from 'lucide-react';

interface OfficialTranscriptModalProps {
  transcript: AcademicTranscriptReport;
  onClose: () => void;
}

export const OfficialTranscriptModal: React.FC<OfficialTranscriptModalProps> = ({ transcript, onClose }) => {
  const handlePrint = () => {
    window.print();
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4 overflow-y-auto">
      <div className="bg-white rounded-2xl shadow-2xl border border-slate-200 w-full max-w-4xl my-8 overflow-hidden animate-scaleIn print:m-0 print:border-none print:shadow-none">
        {/* Top Control Bar (Hidden when printing) */}
        <div className="px-6 py-3.5 bg-slate-900 text-white flex items-center justify-between print:hidden">
          <div className="flex items-center gap-2 text-sm font-semibold">
            <Award className="w-4 h-4 text-amber-400" />
            Official Academic Credential
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={handlePrint}
              className="inline-flex items-center gap-1.5 px-3 py-1.5 bg-indigo-600 hover:bg-indigo-700 text-white rounded-lg text-xs font-medium shadow-sm transition-colors"
            >
              <Printer className="w-3.5 h-3.5" />
              Print / Save PDF
            </button>
            <button
              onClick={onClose}
              className="p-1 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800 transition-colors"
            >
              <X className="w-5 h-5" />
            </button>
          </div>
        </div>

        {/* Printable Transcript Document */}
        <div className="p-8 sm:p-12 space-y-8 bg-white print:p-4 text-slate-900 font-serif">
          {/* Document Header */}
          <div className="text-center pb-6 border-b-2 border-slate-900 space-y-1">
            <div className="flex justify-center mb-2">
              <div className="p-3 bg-indigo-900 text-amber-400 rounded-full print:bg-slate-900">
                <GraduationCap className="w-10 h-10" />
              </div>
            </div>
            <h1 className="text-2xl font-bold uppercase tracking-wide text-slate-900 font-sans">
              {transcript.header.universityName}
            </h1>
            <p className="text-sm uppercase tracking-wider text-slate-600 font-sans font-medium">
              Office of the Academic Registrar • Official Transcript of Record
            </p>
            <div className="flex flex-wrap justify-center items-center gap-4 text-xs text-slate-500 font-mono pt-2">
              <span>Transcript ID: {transcript.header.transcriptNumber}</span>
              <span>•</span>
              <span>Issued: {new Date(transcript.header.issueDate).toLocaleDateString()}</span>
              <span>•</span>
              <span>Verification Hash: {transcript.header.verificationHash}</span>
            </div>
          </div>

          {/* Student Bio Grid */}
          <div className="bg-slate-50 border border-slate-200 rounded-xl p-5 grid grid-cols-2 sm:grid-cols-4 gap-4 font-sans text-xs">
            <div>
              <span className="text-slate-500 uppercase block font-semibold text-[10px]">Student Name</span>
              <span className="font-bold text-slate-900 text-sm">{transcript.studentBio.fullName}</span>
            </div>
            <div>
              <span className="text-slate-500 uppercase block font-semibold text-[10px]">Roll / Matric Number</span>
              <span className="font-bold text-slate-900 text-sm font-mono">{transcript.studentBio.rollNumber}</span>
            </div>
            <div>
              <span className="text-slate-500 uppercase block font-semibold text-[10px]">Academic Department</span>
              <span className="font-bold text-slate-900 text-sm">{transcript.studentBio.departmentName}</span>
            </div>
            <div>
              <span className="text-slate-500 uppercase block font-semibold text-[10px]">Current Semester</span>
              <span className="font-bold text-slate-900 text-sm">Semester {transcript.studentBio.currentSemester}</span>
            </div>
          </div>

          {/* Semester Records */}
          <div className="space-y-6">
            {transcript.semesterRecords.length === 0 ? (
              <div className="py-8 text-center text-sm text-slate-400 italic font-sans">
                No course coursework records have been completed yet.
              </div>
            ) : (
              transcript.semesterRecords.map((sem) => (
                <div key={sem.semester} className="space-y-2">
                  <div className="flex items-center justify-between pb-1 border-b border-slate-300 font-sans">
                    <h3 className="font-bold text-sm text-slate-800 uppercase tracking-wide">
                      Semester {sem.semester} Record
                    </h3>
                    <div className="text-xs text-slate-600 font-medium">
                      <span>Term Credits: {sem.semesterCredits}</span>
                      <span className="mx-2">•</span>
                      <span>Term GPA: <strong className="text-indigo-900">{sem.semesterGpa.toFixed(2)}</strong></span>
                    </div>
                  </div>

                  <table className="w-full text-left text-xs font-sans border-collapse">
                    <thead>
                      <tr className="border-b border-slate-200 text-slate-500 font-semibold text-[11px]">
                        <th className="py-2 px-2">Course Code</th>
                        <th className="py-2 px-2">Course Title</th>
                        <th className="py-2 px-2 text-center">Credits</th>
                        <th className="py-2 px-2 text-center">Weighted Score</th>
                        <th className="py-2 px-2 text-center">Letter Grade</th>
                        <th className="py-2 px-2 text-center">Grade Point</th>
                        <th className="py-2 px-2">Remark</th>
                      </tr>
                    </thead>
                    <tbody className="divide-y divide-slate-100">
                      {sem.courses.map((course) => (
                        <tr key={course.courseCode}>
                          <td className="py-2 px-2 font-mono font-bold text-slate-800">{course.courseCode}</td>
                          <td className="py-2 px-2 font-medium text-slate-900">{course.courseTitle}</td>
                          <td className="py-2 px-2 text-center font-medium">{course.credits}</td>
                          <td className="py-2 px-2 text-center">{course.marksObtained.toFixed(1)}%</td>
                          <td className="py-2 px-2 text-center font-bold text-indigo-900">{course.letterGrade}</td>
                          <td className="py-2 px-2 text-center font-medium">{course.gradePoint.toFixed(1)}</td>
                          <td className="py-2 px-2 text-slate-500 text-[11px]">{course.remark}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              ))
            )}
          </div>

          {/* Cumulative Summary Ribbon */}
          <div className="bg-indigo-950 text-white rounded-xl p-6 font-sans flex flex-col sm:flex-row items-center justify-between gap-6 print:bg-slate-900">
            <div className="space-y-1 text-center sm:text-left">
              <span className="text-xs uppercase tracking-wider text-indigo-300 font-medium">Overall Standing</span>
              <div className="text-lg font-bold text-amber-300 flex items-center justify-center sm:justify-start gap-2">
                <CheckCircle2 className="w-5 h-5 text-emerald-400" />
                {transcript.summary.academicStanding}
              </div>
            </div>

            <div className="flex items-center gap-8 text-center">
              <div>
                <span className="text-[11px] uppercase text-indigo-300 block">Credits Earned</span>
                <span className="text-xl font-bold">
                  {transcript.summary.totalCreditsEarned} / {transcript.summary.totalCreditsAttempted}
                </span>
              </div>
              <div className="border-l border-indigo-800 pl-8">
                <span className="text-[11px] uppercase text-indigo-300 block">Cumulative GPA</span>
                <span className="text-3xl font-extrabold text-amber-400 font-mono">
                  {transcript.summary.cumulativeGpa.toFixed(2)}
                </span>
                <span className="text-xs text-indigo-300"> / 4.00</span>
              </div>
            </div>
          </div>

          {/* Signatures & Accreditation Footer */}
          <div className="pt-6 border-t border-slate-200 font-sans text-xs flex flex-col sm:flex-row items-center justify-between gap-6">
            <div className="flex items-center gap-3">
              <div className="p-2.5 rounded-full bg-slate-100 border border-slate-300">
                <ShieldCheck className="w-6 h-6 text-emerald-600" />
              </div>
              <div>
                <div className="font-bold text-slate-800">{transcript.signatures.registrarName}</div>
                <div className="text-slate-500 text-[11px]">Officially Certified by Academic Registrar</div>
              </div>
            </div>

            <div className="text-right text-[11px] text-slate-500 font-mono">
              <div>Seal: {transcript.signatures.officialSeal}</div>
              <div className="text-indigo-600 hover:underline">{transcript.signatures.verificationUrl}</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

